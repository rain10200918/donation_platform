package com.example.donationplatform.mapper;

import com.example.donationplatform.entity.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserAuthMapper {
    @Select("select * from user_auth where id = #{id}")
    UserAuth getById(Long id);
    @Select("select * from user_auth where real_name = #{realName} and id_number = #{idNumber}")
    UserAuth getByrealNameAndIdNumber(String realName, String idNumber);

    void insert(UserAuth auth);

    void updateById(UserAuth existing);
}
