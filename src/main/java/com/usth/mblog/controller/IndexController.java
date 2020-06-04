package com.usth.mblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.usth.mblog.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{

    @RequestMapping({"/","/index"})
    public String index() {

        //获取分页信息
        IPage<PostVo> results = postService.paging(getPage(),null,null,null,null,"created");

        request.setAttribute("pageData",results);
        request.setAttribute("currentCategoryId",0);
        return "index";
    }

}
