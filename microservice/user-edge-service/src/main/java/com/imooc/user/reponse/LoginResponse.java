package com.imooc.user.reponse;

/**
 * Created by sww_6 on 2019/3/4.
 */
public class LoginResponse extends Reponse {
  private String token;
  public LoginResponse(String token) {
    this.token = token;
  }
}
