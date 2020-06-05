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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class ViewCountSyncTask {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PostService postService;

    /**
     * 定时任务：每12小时同步一次redis和mysql中文章点击量信息
     */
    @Scheduled(cron = "0 0 0/12 * * *")
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
                redisUtil.del(key);
                log.info(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN) +
                        "---Post:" + id + " ---------------------> " + "更新成功");
            }
        }
    }
}
