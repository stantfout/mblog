package com.usth.mblog.search.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息队列的消息实体类
 */
@Data
@AllArgsConstructor
public class PostMqIndexMessage implements Serializable {

    public final static String CREATE_OR_UPDATE = "create_or_update";
    public final static String REMOVE = "remove";

    private Long postId;
    private String type;

}
