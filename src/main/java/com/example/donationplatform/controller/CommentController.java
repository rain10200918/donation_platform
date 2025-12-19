package com.example.donationplatform.controller;

import cn.hutool.core.util.StrUtil;
import com.example.donationplatform.entity.Comment;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.service.CommentService;
import com.example.donationplatform.util.UserContext;
import com.example.donationplatform.vo.CommentVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @GetMapping("/list/{projectId}")
    public Result list(@PathVariable Long projectId,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize){
        // 1. 分页查询根评论
        if (projectId == null || projectId <= 0 || pageNum <= 0 || pageSize <= 0){
            return Result.error("参数错误");
        }
        PageInfo<CommentVO> list = commentService.list(projectId, pageNum, pageSize);
        return Result.success(list);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment){
        Users user = UserContext.getUser();
        if (user == null) return Result.error("请先登录后发表言论");

        // 2. 基本校验
        if (StrUtil.isBlank(comment.getContent())) return Result.error("评论内容不能为空");
        if (comment.getProjectId() == null) return Result.error("项目ID不能为空");

        comment.setUserId(user.getId());
        boolean saved = commentService.saveComment(comment);
        return saved ? Result.success("留言成功") : Result.error("留言失败");
    }

}
