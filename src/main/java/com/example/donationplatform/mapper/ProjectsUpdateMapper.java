package com.example.donationplatform.mapper;

import com.example.donationplatform.entity.ProjectUpdates;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectsUpdateMapper {
    @Select("select * from project_updates where project_id = #{id}")
    List<ProjectUpdates> getAllByProjectId(Long id);

    void save(ProjectUpdates projectUpdates);
}
