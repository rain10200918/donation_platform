package com.example.donationplatform.mapper;

import com.example.donationplatform.entity.Projects;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectsMapper {
    @Select("select * from projects where status=1 and audit_status=1")
    List<Projects> getAllProject();

    void save(Projects projects);
    @Select("select * from projects where id=#{id}")
    Projects getById(Long id);
    @Select("select * from projects where initiator_id = #{id}")
    List<Projects> getByInitiatorId(Long id);
    @Select("select * from projects where audit_status = #{status}")
    List<Projects> getByStatus(String status);
    @Select("select count(*) from projects where audit_status = #{status}")
    int countByStatus(int i);
    @Select("select sum(target_amount) from projects")
    int sumTargetAmount();
}
