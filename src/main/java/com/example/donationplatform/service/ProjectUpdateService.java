package com.example.donationplatform.service;

import com.example.donationplatform.entity.ProjectUpdates;
import com.example.donationplatform.entity.Result;

import java.util.List;

public interface ProjectUpdateService {
    List<ProjectUpdates> getByProjectId(Long id);

    void save(ProjectUpdates projectUpdates);
}
