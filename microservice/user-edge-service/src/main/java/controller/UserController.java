package controller;

import com.imooc.thrift.user.UserInfo;
import dto.UserDTO;

import java.security.MessageDigest;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
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

    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
    @ResponseBody
    public Reponse sendVerifyCode(@RequestParam(value = "mobile", required = false) String mobile,
                                  @RequestParam(value = "email", required = false) String email) {

        boolean result = false;
        String message = "Verify code is:";
        String code = randomCode("1234567890", 6);
        try {
            if (StringUtils.isNotBlank("mobile")) {
                serviceProvider.getMessageService().send_sendMobileMessage(mobile, message + code);
                redisClient.set(mobile, code);
            } else if (StringUtils.isNotBlank("email")) {
                serviceProvider.getMessageService().send_sendEmailMessage(email, message + code);
                redisClient.set(email, code);
            } else {
                return Reponse.USERNAME_PASSWORD_INVALID;
            }
        } catch (TException e) {
            e.printStackTrace();
        }
        if (!result) {
            return Reponse.SEND_VERIFYCODE_FAILED;
        }
        return Reponse.SUCCESS;
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
            String redisCode = redisClient.get(mobile);
            if (!verifyCode.equals(redisCode)) {
                return Reponse.VERIFY_CODE_INVALID;
            }
        } else {
            String emailCode = redisClient.get(email);
            if (verifyCode.equals(emailCode)) {
                return Reponse.VERIFY_CODE_INVALID;
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(md5(password));
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        return Reponse.SUCCESS;
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
