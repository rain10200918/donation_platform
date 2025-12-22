package com.example.donationplatform.mapper;

import com.example.donationplatform.dto.StatusDTO;
import com.example.donationplatform.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UsersMapper {


    @Select("select * from users where mobile = #{mobile}")
    Users selectByMobile(String mobile);
    @Select("select * from users where id = #{userId}")
    Users selectById(long userId);

    void updateUserLoginInfo(Users user);
    @Select("select * from users where email = #{email}")
    Users selectByEmail(String email);

    void registerUser(Users users);
    @Select("select * from users where id = #{id}")
    Users getUserById(Long id);

    List<Users> getUserList(Integer pageNum, Integer pageSize, String nickName, String status);
    @Update("update users set status = #{status} where id = #{userId}")
    void updateUserStatus(StatusDTO dto);
    @Update("update users set balance = balance + #{amount} ,update_time = NOW() where id = #{id}")
    void updateById(BigDecimal amount ,Long id);
}
