package com.usth.mblog.schedules;
import	java.util.Date;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.usth.mblog.controller.PostController;
import com.usth.mblog.entity.Post;
import com.usth.mblog.service.PostService;
import com.usth.mblog.util.RedisKeyUtil;
import com.usth.mblog.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ViewCountSyncTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PostService postService;

    @Scheduled(cron = "0 59 23 * * *")
    public void task() {
        Set<String> keys = redisTemplate.keys(RedisKeyUtil.getPostKey("*"));
        if (keys == null) {
            return;
        }
        for (String key : keys) {
            if (redisUtil.hHasKey(key,RedisKeyUtil.getPostViewCountKey())) {
                Integer id = (Integer) redisUtil.hget(key, RedisKeyUtil.getPostIdKey());
                Integer viewCount = (Integer) redisUtil.hget(key, RedisKeyUtil.getPostViewCountKey());
                postService.update(new UpdateWrapper<Post>()
                        .eq("id",id)
                        .set("view_count", viewCount));
                LOGGER.info(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN) +
                        "---Post:" + id + " ---------------------> " + "更新成功");
            }
        }
    }
}
