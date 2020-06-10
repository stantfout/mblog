package com.usth.mblog.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 搜索信息的实体类
 */
@Data
@Document(indexName = "post", type = "post")
public class PostDocument implements Serializable {
    @Id
    private Long id;
    // ik分词器
    @Field(type = FieldType.Text, store = true, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Long)
    private Long authorId;
    @Field(type = FieldType.Keyword)
    private String authorName;
    private String authorAvatar;

    private Integer level;
    private Boolean recommend;

    private Integer commentCount;
    private Integer viewCount;

    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Date)
    private Date created;
}
