package reponse;

import java.io.Serializable;

/**
 * Created by sww_6 on 2019/3/4.
 */
public class Reponse implements Serializable {
  public static final Reponse USERNAME_PASSWORD_INVALID = new Reponse("1001", "username or password invalid");

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
