package com.example.donationplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.example.donationplatform.entity.Comment;
import com.example.donationplatform.mapper.CommentMapper;
import com.example.donationplatform.mapper.DonationMapper;
import com.example.donationplatform.service.CommentService;
import com.example.donationplatform.vo.CommentVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DonationMapper donationMapper;

    @Override
    public PageInfo<CommentVO> list(Long projectId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> rootEntities = commentMapper.selectRootComments(projectId);
        PageInfo<Comment> rootPageInfo = new PageInfo<>(rootEntities);

        if (CollUtil.isEmpty(rootEntities)) {
            return new PageInfo<>(new ArrayList<>());
        }

        // 1. 获取 ID 集合（改用 map 避免静态引用问题）
        List<Long> rootIds = CollUtil.map(rootEntities, entity -> entity.getId(), true);

        // 2. 批量查出子评论
        List<CommentVO> allChildren = commentMapper.selectChildrenByParentIds(rootIds);

        // 3. 实体转 VO
        List<CommentVO> rootVOs = BeanUtil.copyToList(rootEntities, CommentVO.class);

        // 4. 组装树
        for (CommentVO rootVO : rootVOs) {
            // 过滤出父 ID 等于当前根 ID 的评论
            List<CommentVO> children = allChildren.stream()
                    .filter(child -> child.getParentId().equals(rootVO.getId()))
                    .collect(Collectors.toList());
            rootVO.setChildren(children);
        }

        PageInfo<CommentVO> resultPage = new PageInfo<>(rootVOs);
        resultPage.setTotal(rootPageInfo.getTotal());
        return resultPage;
    }

    @Override
    @Transactional
    public boolean saveComment(Comment comment) {
        Integer countSuccess = donationMapper.countSuccessDonation(comment.getUserId(), comment.getProjectId());
        comment.setIsDonor(countSuccess > 0 ? "1" : "0");
        if (comment.getParentId() == null){
            comment.setParentId(0L);
        }
        if (comment.getStatus() == null){
            comment.setStatus("1");
        }
        return commentMapper.insert(comment);
    }
}
