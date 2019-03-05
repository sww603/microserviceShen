package controller;

import com.imooc.thrift.user.UserInfo;
import java.util.Random;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  private ServiceProvider serviceProvider;
  public Reponse login(@RequestParam("username") String username,
      @RequestParam("password") String password) {

    //1. 验证用户名密码
    UserInfo userInfo=null;
    try {
      userInfo = serviceProvider.getUserService().getUserByName(username);
    } catch (TException e) {
      e.printStackTrace();
    }

    //2. 生成token
    String token=getToken();
    //3. 缓存用户
    return new LoginResponse(token);
  }

  private String getToken(){
    return randomCode("0123456789abcdefghijklmnopqrstuvwxyz",32);
  }
  private String randomCode(String s,int size){
    StringBuilder result = new StringBuilder(size);

    Random random=new Random();
    for (int i=0;i<size;i++){
      int loc = random.nextInt(s.length());
      result.append(s.charAt(loc));
    }
    return result.toString();
  }
}
