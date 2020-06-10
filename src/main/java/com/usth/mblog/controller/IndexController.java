package com.usth.mblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sun.org.apache.regexp.internal.RE;
import com.usth.mblog.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class IndexController extends BaseController{

    @RequestMapping({"/","/index"})
    public String index(@RequestParam(defaultValue = "created")String order,
                        @RequestParam(defaultValue = "0")Integer recommend) {
        Boolean flag = recommend == 1 ? true : null;
        //获取分页信息
        IPage<PostVo> results = postService.paging(getPage(),null,null,null,flag,order);

        request.setAttribute("pageData",results);
        request.setAttribute("currentCategoryId",0);
        request.setAttribute("order",order);
        request.setAttribute("recommend",recommend);
        return "index";
    }

    @RequestMapping("/search")
    public String search(String q) {

        request.setAttribute("currentCategoryId",0);
        IPage pageData = searchService.search(getPage(), q);
        request.setAttribute("q",q);
        request.setAttribute("pageData",pageData);
        return "search";
    }
}
