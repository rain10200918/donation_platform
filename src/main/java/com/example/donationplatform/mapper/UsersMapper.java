package com.example.donationplatform.mapper;

import com.example.donationplatform.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UsersMapper {
    @Select("select * from users where mobile = #{mobile}")
    Users selectByMobile(String mobile);
    @Select("select * from users where id = #{userId}")
    Users selectById(long userId);
}
