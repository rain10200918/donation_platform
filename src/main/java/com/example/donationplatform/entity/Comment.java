package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long projectId;
    private Long userId;
    private Long replyUserId;
    private Long parentId;
    private String content;
    private String isDonor;
    private String status;
    private LocalDateTime createTime;
    private String nickName;
    private String avatar;
}
