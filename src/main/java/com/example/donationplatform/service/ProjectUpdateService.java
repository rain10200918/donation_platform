package com.example.donationplatform.service;

import com.example.donationplatform.entity.ProjectUpdates;

import java.util.List;

public interface ProjectUpdateService {
    List<ProjectUpdates> getByProjectId(Long id);
}
