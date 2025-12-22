package com.example.donationplatform.controller;

import com.example.donationplatform.entity.*;
import com.example.donationplatform.mapper.UserAuthMapper;
import com.example.donationplatform.service.ProjectService;
import com.example.donationplatform.service.ProjectUpdateService;
import com.example.donationplatform.service.UserAuthService;
import com.example.donationplatform.util.UserContext;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private ProjectUpdateService projectUpdateService;

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        if (page < 1 || size < 1 || size > 100) {
            return Result.error("分页参数不合法");
        }
        PageInfo<Projects> projectsPageInfo = projectService.pageGet(page, size);
        return Result.success(projectsPageInfo);
    }
    @PostMapping("/publish")
    public Result publish(@RequestBody Projects projects){
        Users user = UserContext.getUser();
        if (user == null) return Result.error("未登录");
        if (projects.getTitle() == null || projects.getPicture() == null) {
            return Result.error("项目标题和封面图不能为空");
        }
        UserAuth userauth = userAuthService.getDetailById(user.getId());
        if (userauth == null) return Result.error("用户未认证");
        if (userauth.getAuthStatus() != 1) return Result.error("未通过实名认证，无法发起项目");
        projects.setInitiatorId(user.getId());
        projects.setAuditStatus(0);
        projects.setStatus(0);
        projects.setPicture(projects.getPicture());
        projects.setRaisedAmount(BigDecimal.ZERO);
        projects.setStartTime(LocalDateTime.now());
        projects.setCreateTime(LocalDateTime.now());
        projects.setUpdateTime(LocalDateTime.now());
        projectService.save(projects);
        return Result.success("发布成功，请耐心等待系统审核");
    }
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id){
        Projects projects = projectService.getById(id);
        if (projects == null) return Result.error("项目不存在");
        List<ProjectUpdates> byProjectId = projectUpdateService.getByProjectId(id);
        Map<String,Object> map = new HashMap<>();
        map.put("project",projects);
        map.put("updates",byProjectId);
        return Result.success(map);
    }
    @GetMapping("/my-projects")
    public Result myProjects(){
        Users user = UserContext.getUser();
        if (user == null){
            return Result.error("未登录");
        }
        List<Projects> projects = projectService.getByInitiatorId(user.getId());
        return Result.success(projects);
    }
}
