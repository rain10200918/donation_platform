package com.example.donationplatform.service.impl;

import com.example.donationplatform.entity.Projects;
import com.example.donationplatform.mapper.ProjectsMapper;
import com.example.donationplatform.service.ProjectService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectsMapper projectsMapper;
    @Override
    public PageInfo<Projects> pageGet(int page, int size) {
        PageHelper.startPage(page, size);
        List<Projects> allProject = projectsMapper.getAllProject();
        return new PageInfo<>(allProject);
    }

    @Override
    public void save(Projects projects) {
        projectsMapper.save(projects);
    }

    @Override
    public Projects getById(Long id) {
        return projectsMapper.getById(id);
    }

    @Override
    public List<Projects> getByInitiatorId(Long id) {
        return projectsMapper.getByInitiatorId(id);
    }
}
