package controller;

import com.imooc.thrift.user.UserInfo;
import dto.UserDTO;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.RedisClient;
import reponse.LoginResponse;
import reponse.Reponse;
import thrift.ServiceProvider;

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

    if (StringUtils.isBlank("mobile") && StringUtils.isBlank("email")) {
      return Reponse.MOBILE_OR_EMAIL_REQUIRED;
    }
    if (StringUtils.isNotBlank("mobile")) {
      //ServiceProvider
    } else {

    }
    return null;
  }

  @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
  @ResponseBody
  public Reponse sendVerifyCode(@RequestParam(value = "mobile", required = false) String mobile,
      @RequestParam(value = "email", required = false) String email) {

    return null;

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
}
