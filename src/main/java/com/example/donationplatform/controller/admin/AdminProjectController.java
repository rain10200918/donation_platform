package com.example.donationplatform.controller.admin;

import com.example.donationplatform.annotation.AdminOnly;
import com.example.donationplatform.dto.AuditDTO;
import com.example.donationplatform.dto.StatusDTO;
import com.example.donationplatform.entity.Projects;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.service.ProjectService;
import com.example.donationplatform.service.UsersService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UsersService usersService;
    @GetMapping("/project/list")
    @AdminOnly
    public Result page(@RequestParam(defaultValue = "2") String status ,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        if (pageSize == null || pageSize < 1 || pageSize > 100){
            return Result.error("页大小不能小于1或大于100");
        }
        PageInfo<Projects> byStatus = projectService.getByStatus(status, pageNum, pageSize);
        return Result.success(byStatus);
    }
    @GetMapping("/project/stats")
    @AdminOnly
    public Result getProjectStats(){
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", projectService.countByStatus(0));
        stats.put("approvedCount", projectService.countByStatus(1));
        stats.put("rejectedCount", projectService.countByStatus(2));
        stats.put("totalAmount", projectService.sumTargetAmount());
        return Result.success(stats);
    }
    @GetMapping("/user/list")
    @AdminOnly
    public Result getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) String status
    ){
      if (pageSize == null || pageSize < 1 || pageSize > 100){
          return Result.error("页大小不能小于1或大于100");
      }
      PageInfo<Users> userList = usersService.getUserList(pageNum, pageSize, nickName, status);
      return Result.success(userList);
    }

    @PostMapping("/user/status")
    @AdminOnly
    public Result updateUserStatus(
           @RequestBody StatusDTO dto
    ){
        usersService.updateUserStatus(dto);
        return Result.success("更新成功");
    }
    @GetMapping("/user/{id}")
    @AdminOnly
    public Result detailUser(@PathVariable Long id){
        return null;
    }
    @PostMapping("/project/audit")
    @AdminOnly
    public Result auditProject(
            @RequestBody AuditDTO dto
    ){
        if (dto.getAuditStatus() == null){
            return Result.error("请选择审核状态");
        }
        if (dto.getAuditStatus() == 2 && dto.getRejectReason() == null){
            return Result.error("请填写拒绝理由");
        }
        projectService.updateProjectAuditStatus(dto);
        return Result.success("审核成功");
    }
}
