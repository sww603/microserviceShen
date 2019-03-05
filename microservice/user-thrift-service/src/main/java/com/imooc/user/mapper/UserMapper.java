package com.imooc.user.mapper;

import com.imooc.thrift.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by sww_6 on 2019/3/2.
 */
@Mapper
public interface UserMapper {
  @Select("SELECT id,username,`password`,real_name as realName,mobile,email FROM pe_user WHERE id = #{id}")
  UserInfo getUserById(@Param("id") int id);

  @Select("SELECT id,username,`password`,real_name as realName,mobile,email FROM pe_user WHERE username = #{username}")
  UserInfo getUserByName(@Param("id") String username);

  @Select("INSERT INTO pe_user (username,password,real_name,mobile,email) VALUES (#{u.username},#{u.password},#{u.realName},#{u.mobile},#{email})")
  void registerUser(@Param("u") UserInfo userInfo);
}
