package com.usth.mblog.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usth.mblog.entity.Category;
import com.usth.mblog.service.CategoryService;
import com.usth.mblog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * 初始化
 */
@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    CategoryService categoryService;

    ServletContext servletContext;

    @Autowired
    PostService postService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化分类列表
        List<Category> categories = categoryService.list(new QueryWrapper<Category>().eq("status", 0));
        servletContext.setAttribute("categories", categories);

        postService.initWeekRank();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
