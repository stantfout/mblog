package com.usth.mblog.service;

public interface WsService {

    /**
     * websocket即时通讯
     * @param toUserId
     */
    void sendMessageCountToUser(Long toUserId);
}
