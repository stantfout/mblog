package com.usth.mblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usth.mblog.entity.UserMessage;
import com.usth.mblog.service.UserMessageService;
import com.usth.mblog.service.WsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsServiceImpl implements WsService {

    @Autowired
    UserMessageService messageService;

    @Autowired
    SimpMessagingTemplate messageTemplate;

    @Async
    @Override
    public void sendMessageCountToUser(Long toUserId) {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id",toUserId)
                .eq("status",0));

        // webSocket通知 "/user/" + ${profile.id} + "/messCount"
        messageTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);
    }
}
