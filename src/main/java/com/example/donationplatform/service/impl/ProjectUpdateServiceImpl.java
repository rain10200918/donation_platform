package com.example.donationplatform.service.impl;

import com.example.donationplatform.entity.ProjectUpdates;
import com.example.donationplatform.mapper.ProjectsUpdateMapper;
import com.example.donationplatform.service.ProjectUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectUpdateServiceImpl implements ProjectUpdateService {
    @Autowired
    private ProjectsUpdateMapper projectsUpdateMapper;
    @Override
    public List<ProjectUpdates> getByProjectId(Long id) {
        List<ProjectUpdates> allByProjectId = projectsUpdateMapper.getAllByProjectId(id);
        return allByProjectId;
    }

    @Override
    public void save(ProjectUpdates projectUpdates) {
        projectsUpdateMapper.save(projectUpdates);
    }
}
