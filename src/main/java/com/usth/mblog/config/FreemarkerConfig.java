package com.usth.mblog.config;

import com.usth.mblog.template.PostsTemplate;
import com.usth.mblog.template.TimeAgoMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {
    @Autowired
    private freemarker.template.Configuration configuration;

    @Autowired
    PostsTemplate postsTemplate;

    @PostConstruct
    public void setUp() {
        configuration.setSharedVariable("timeAgo", new TimeAgoMethod());
        configuration.setSharedVariable("posts", postsTemplate);
    }

}
