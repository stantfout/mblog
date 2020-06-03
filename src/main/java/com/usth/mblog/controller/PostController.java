package com.usth.mblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Post;
import com.usth.mblog.vo.CommentVo;
import com.usth.mblog.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PostController extends BaseController {

    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable Long id) {
        //设置当前页面的分类id，用于前端高亮显示
        request.setAttribute("currentCategoryId",id);

        IPage<PostVo> results = postService.paging(getPage(),id,null,null,null,"created");

        request.setAttribute("pageData",results);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable Long id) {
        PostVo vo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id",id));
        Assert.notNull(vo,"文章已经被删除");

        IPage<CommentVo> result = commentService.paging(getPage(),vo.getId(),null,"created");

        request.setAttribute("currentCategoryId",vo.getCategoryId());
        request.setAttribute("post",vo);
        request.setAttribute("pageData",result);
        return "post/detail";
    }
}
