package com.example.donationplatform.service;

import com.example.donationplatform.entity.Projects;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ProjectService {
    PageInfo<Projects> pageGet(int page, int size);

    void save(Projects projects);

    Projects getById(Long id);

    List<Projects> getByInitiatorId(Long id);

    PageInfo<Projects> getByStatus(String status, Integer pageNum, Integer pageSize);
}
