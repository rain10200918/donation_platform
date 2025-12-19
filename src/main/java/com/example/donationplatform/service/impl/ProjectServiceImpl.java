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

    @Override
    public PageInfo<Projects> getByStatus(String status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Projects> byStatus = projectsMapper.getByStatus(status);
        return new PageInfo<>(byStatus);
    }

    @Override
    public int countByStatus(int i) {
        return projectsMapper.countByStatus(i);
    }

    @Override
    public int sumTargetAmount() {
        return projectsMapper.sumTargetAmount();
    }
}
