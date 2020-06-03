package com.usth.mblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Post;
import com.usth.mblog.mapper.PostMapper;
import com.usth.mblog.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usth.mblog.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    PostMapper postMapper;

    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if (level == null) {
            level = -1;
        }

        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);
        return postMapper.selectPosts(page,wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<Post> wrapper) {
        return postMapper.selectOnePost(wrapper);
    }
}
