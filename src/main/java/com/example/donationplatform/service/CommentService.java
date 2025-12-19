package com.example.donationplatform.service;

import com.example.donationplatform.entity.Comment;
import com.example.donationplatform.vo.CommentVO;
import com.github.pagehelper.PageInfo;

public interface CommentService {
    PageInfo<CommentVO> list(Long projectId, Integer pageNum, Integer pageSize);

    boolean saveComment(Comment comment);
}
