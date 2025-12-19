package com.example.donationplatform.mapper;

import com.example.donationplatform.entity.Comment;
import com.example.donationplatform.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectRootComments(Long projectId);

    List<CommentVO> selectChildrenByParentIds(List<Long> rootIds);

    boolean insert(Comment comment);
}
