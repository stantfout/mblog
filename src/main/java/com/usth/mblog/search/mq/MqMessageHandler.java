package com.usth.mblog.search.mq;

import com.usth.mblog.config.RabbitConfig;
import com.usth.mblog.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息队列监听者
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConfig.es_queue)
public class MqMessageHandler {

    @Autowired
    SearchService searchService;

    @RabbitHandler
    public void handle(PostMqIndexMessage message) {

        log.info("mq 收到一条消息 -----> {}",message.toString());

        if (message.getType().equals(PostMqIndexMessage.CREATE_OR_UPDATE)) {
            searchService.createOrUpdate(message);
        } else if (message.getType().equals(PostMqIndexMessage.REMOVE)){
            searchService.removeIndex(message);
        } else {
            log.error("没找到对应的消息类型------>" + message.toString());
        }
    }
}
