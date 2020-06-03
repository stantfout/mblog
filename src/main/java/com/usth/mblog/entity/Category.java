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
public class Category extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String name;

    /**
     * 内容描述
     */
    private String content;

    private String summary;

    /**
     * 图标
     */
    private String icon;

    /**
     * 该分类的内容数量
     */
    private Integer postCount;

    /**
     * 排序编码
     */
    private Integer orderNum;

    /**
     * 父级分类的ID
     */
    private Long parentId;

    /**
     * SEO关键字
     */
    private String metaKeywords;

    /**
     * SEO描述内容
     */
    private String metaDescription;

    /**
     * 创建日期
     */
    private Date created;

    private Date modified;

    private Integer status;


}
