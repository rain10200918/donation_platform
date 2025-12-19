package com.example.donationplatform.controller.admin;

import com.example.donationplatform.entity.Projects;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.service.ProjectService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminProjectController {
    @Autowired
    private ProjectService projectService;
    @GetMapping("/project/list")
    public Result page(@RequestParam(defaultValue = "2") String status ,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        if (pageSize == null || pageSize < 1 || pageSize > 100){
            return Result.error("页大小不能小于1或大于100");
        }
        PageInfo<Projects> byStatus = projectService.getByStatus(status, pageNum, pageSize);
        return Result.success(byStatus);
    }

}
