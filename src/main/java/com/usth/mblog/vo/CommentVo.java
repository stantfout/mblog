package com.usth.mblog.vo;

import com.usth.mblog.entity.Comment;
import lombok.Data;

@Data
public class CommentVo extends Comment {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
}
