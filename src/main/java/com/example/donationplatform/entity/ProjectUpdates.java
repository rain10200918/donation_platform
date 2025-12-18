package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * ProjectUpdates类用于表示项目更新信息
 * 该类包含了项目更新的基本属性，如更新类型、内容、图片等信息
 */
public class ProjectUpdates {
    /**
     * 更新记录的唯一标识符
     */
    private Long id;

    /**
     * 关联的项目ID
     */
    private Long projectId;

    /**
     * 更新类型标识
     */
    private Integer updateType;

    /**
     * 更新内容描述
     */
    private String content;

    /**
     * 更新相关的图片信息，可能包含多个图片的存储路径或标识
     */
    private String images;

    /**
     * 更新记录的创建时间
     */
    private LocalDateTime createTime;
}
