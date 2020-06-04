package com.usth.mblog.schedules;

import com.usth.mblog.controller.PostController;
import com.usth.mblog.service.PostService;
import com.usth.mblog.service.impl.PostServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HotsPostSyncTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostService postService;

    @Scheduled(cron = "0 5 0 * * *")
    public void task() {
        postService.updateWeekRank();
    }
}
