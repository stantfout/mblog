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
public class Post extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 编辑模式：html可视化，markdown ..
     */
    private String editMode;

    private Long categoryId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支持人数
     */
    private Integer voteUp;

    /**
     * 反对人数
     */
    private Integer voteDown;

    /**
     * 访问量
     */
    private Integer viewCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 是否为精华
     */
    private Boolean recommend;

    /**
     * 置顶等级
     */
    private Integer level;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建日期
     */
    private Date created;

    /**
     * 最后更新日期
     */
    private Date modified;


}
