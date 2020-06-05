package com.usth.mblog.schedules;

import com.usth.mblog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    }
}
