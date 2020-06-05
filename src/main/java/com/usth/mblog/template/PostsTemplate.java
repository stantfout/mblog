package com.usth.mblog.template;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.common.templates.DirectiveHandler;
import com.usth.mblog.common.templates.TemplateDirective;
import com.usth.mblog.service.PostService;
import com.usth.mblog.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文章分页信息
 */
@Component
public class PostsTemplate extends TemplateDirective {

    @Autowired
    PostService postService;

    @Override
    public String getName() {
        return "posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer level = handler.getInteger("level");
        Integer pn = handler.getInteger("pn",1);
        Integer size = handler.getInteger("size",5);
        Long categoryId = handler.getLong("categoryId");

        IPage<PostVo> page = postService.paging(new Page(pn, size), categoryId, null, level, null, "created");

        handler.put(RESULTS,page).render();

    }
}
