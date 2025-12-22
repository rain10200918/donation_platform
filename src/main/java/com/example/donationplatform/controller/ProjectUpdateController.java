package com.example.donationplatform.controller;

import com.example.donationplatform.entity.ProjectUpdates;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.service.ProjectUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projectUpdate")
public class ProjectUpdateController {
    @Autowired
    private ProjectUpdateService projectUpdateService;
    @PostMapping("save")
    public Result save(@RequestBody ProjectUpdates projectUpdates){
       if (projectUpdates.getProjectId() == null){
           return Result.error("项目id不能为空");
       }
       if (projectUpdates.getContent() == null){
           return Result.error("更新内容不能为空");
       }
       if (projectUpdates.getUpdateType() == null){
           return Result.error("更新类型不能为空");
       }
        projectUpdateService.save(projectUpdates);
       return Result.success("更新成功");

    }
}
