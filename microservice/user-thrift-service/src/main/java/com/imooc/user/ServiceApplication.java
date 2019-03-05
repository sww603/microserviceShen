package com.imooc.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by sww_6 on 2019/3/2.
 */
@SpringBootApplication
public class ServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceApplication.class, args);
    System.out.println("沈雯雯启动成功！");
  }
}
