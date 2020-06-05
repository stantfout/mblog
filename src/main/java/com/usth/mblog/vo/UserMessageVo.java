package com.usth.mblog.vo;

import com.usth.mblog.entity.UserMessage;
import lombok.Data;

@Data
public class UserMessageVo extends UserMessage {

    private String fromUserName;
    private String postTitle;
}
