package com.usth.mblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.service.CommentService;
import com.usth.mblog.service.PostService;
import com.usth.mblog.service.UserService;
import com.usth.mblog.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 整合一些Controller公共的变量和方法
 */
@Controller
public class BaseController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    public Page getPage() {
        //获取当前分页，通过ServletRequestUtils当获取不到时默认为1
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        //获取一页大小，通过ServletRequestUtils当获取不到时默认为10
        int size = ServletRequestUtils.getIntParameter(request, "size", 10);
        return new Page(pn,size);
    }

    /**
     * 获取当前用户
     * @return 当前用户
     */
    protected AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取用户Id
     * @return 用户Id
     */
    protected Long getProfileId() {
        return getProfile().getId();
    }


}
