package com.imooc.user.controller;

import com.imooc.thrift.user.UserInfo;
import com.imooc.user.dto.UserDTO;
import java.security.MessageDigest;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.imooc.user.redis.RedisClient;
import com.imooc.user.reponse.LoginResponse;
import com.imooc.user.reponse.Reponse;
import com.imooc.user.thrift.ServiceProvider;

/**
 * Created by sww_6 on 2019/3/4.
 */
@Controller
@RequestMapping("/user")
public class UserController {

  @Autowired
  private RedisClient redisClient;
  @Autowired
  private ServiceProvider serviceProvider;
  @RequestMapping(value="/login", method = RequestMethod.GET)

  public String login() {
    return "login";
  }
  public Reponse login(@RequestParam("username") String username,
      @RequestParam("password") String password) {

    //1. 验证用户名密码
    UserInfo userInfo = null;
    try {
      userInfo = serviceProvider.getUserService().getUserByName(username);
    } catch (TException e) {
      e.printStackTrace();
    }

    //2. 生成token
    String token = getToken();
    //3. 缓存用户
    redisClient.set(token, toDto(userInfo), 3600);
    return new LoginResponse(token);
  }

  public Reponse regist(@RequestParam("username") String username,
      @RequestParam("password") String password,
      @RequestParam(value = "mobile", required = false) String mobile,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam("verifyCode") String verifyCode) {

    if(StringUtils.isBlank(mobile) && StringUtils.isBlank(email)) {
      return Reponse.MOBILE_OR_EMAIL_REQUIRED;
    }

    if(StringUtils.isNotBlank(mobile)) {
      String redisCode = redisClient.get(mobile);
      if(!verifyCode.equals(redisCode)) {
        return Reponse.VERIFY_CODE_INVALID;
      }
    }else {
      String redisCode = redisClient.get(email);
      if(!verifyCode.equals(redisCode)) {
        return Reponse.VERIFY_CODE_INVALID;
      }
    }
    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(username);
    userInfo.setPassword(md5(password));
    userInfo.setMobile(mobile);
    userInfo.setEmail(email);

    try {
      serviceProvider.getUserService().regiserUser(userInfo);
    } catch (TException e) {
      e.printStackTrace();
      return Reponse.exception(e);
    }

    return Reponse.SUCCESS;
  }

  @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
  @ResponseBody
  public Reponse sendVerifyCode(@RequestParam(value = "mobile", required = false) String mobile,
      @RequestParam(value = "email", required = false) String email) {

    String message = "Verify code is:";
    String code = randomCode("0123456789", 6);
    try {

      boolean result = false;
      if(StringUtils.isNotBlank(mobile)) {

        result = serviceProvider.getMessageService().sendMobileMessage(mobile, message+code);
        redisClient.set(mobile, code);
      } else if(StringUtils.isNotBlank(email)) {
        result = serviceProvider.getMessageService().sendEmailMessage(email, message+code);
        redisClient.set(email, code);
      } else {
        return Reponse.MOBILE_OR_EMAIL_REQUIRED;
      }

      if(!result) {
        return Reponse.SEND_VERIFYCODE_FAILED;
      }
    } catch (TException e) {
      e.printStackTrace();
      return Reponse.exception(e);
    }

    return Reponse.SUCCESS;

  }

  @RequestMapping(value="/authentication", method = RequestMethod.POST)
  @ResponseBody
  public UserDTO authentication(@RequestHeader("token") String token) {

    return redisClient.get(token);
  }

  private String getToken() {
    return randomCode("0123456789abcdefghijklmnopqrstuvwxyz", 32);
  }

  private String randomCode(String s, int size) {
    StringBuilder result = new StringBuilder(size);

    Random random = new Random();
    for (int i = 0; i < size; i++) {
      int loc = random.nextInt(s.length());
      result.append(s.charAt(loc));
    }
    return result.toString();
  }

  private UserDTO toDto(UserInfo userInfo) {
    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(userInfo, userDTO);
    return userDTO;
  }
  private String md5(String password) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] md5Bytes = md5.digest(password.getBytes("utf-8"));
      return HexUtils.toHexString(md5Bytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
