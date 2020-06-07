package com.usth.mblog.schedules;

import com.usth.mblog.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HotsPostSyncTask {

    @Autowired
    PostService postService;

    /**
     * 定时任务：每天零点5分更新本周热评
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void task() {
        postService.updateWeekRank();
        log.info("7天热评合并成功---------------------->");
    }
}
