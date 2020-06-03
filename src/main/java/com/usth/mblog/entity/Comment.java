package com.usth.mblog.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Comment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 回复的评论ID
     */
    private Long parentId;

    /**
     * 评论的内容ID
     */
    private Long postId;

    /**
     * 评论的用户ID
     */
    private Long userId;

    /**
     * “顶”的数量
     */
    private Integer voteUp;

    /**
     * “踩”的数量
     */
    private Integer voteDown;

    /**
     * 置顶等级
     */
    private Integer level;

    /**
     * 评论的状态
     */
    private Integer status;

    /**
     * 评论的时间
     */
    private Date created;

    /**
     * 评论的更新时间
     */
    private Date modified;


}
