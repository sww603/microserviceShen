package reponse;

import java.io.Serializable;

/**
 * Created by sww_6 on 2019/3/4.
 */
public class Reponse implements Serializable {
  public static final Reponse USERNAME_PASSWORD_INVALID = new Reponse("1001", "username or password invalid");
  public static final Reponse MOBILE_OR_EMAIL_REQUIRED = new Reponse("1002", "mobile or email invalid");
  public static final Reponse SEND_VERIFYCODE_FAILED = new Reponse("1003", "send verify code failed");
  public static final Reponse VERIFY_CODE_INVALID = new Reponse("1004", "verifyCode invalid");

  public static final Reponse SUCCESS = new Reponse();

  private String code;

  private String message;

  public Reponse(){
    this.code = "0";
    this.message = "sucess";
  }

  public Reponse(String code,String message){
    this.code = code;
    this.message = message;
  }

}
