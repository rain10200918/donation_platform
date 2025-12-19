package com.example.donationplatform.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private Long id;
    private Long userId;
    private String nickName;
    private String avatar;
    private String content;
    private Integer isDonor;
    private String createTime;

    // 楼中楼关键字段
    private Long parentId;
    private Long replyUserId;
    private String replyNickName; // 被回复人的名字
    private String status;

    // 子回复列表
    private List<CommentVO> children = new ArrayList<>();
}
