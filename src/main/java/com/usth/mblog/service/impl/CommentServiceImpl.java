package com.usth.mblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Comment;
import com.usth.mblog.mapper.CommentMapper;
import com.usth.mblog.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usth.mblog.vo.CommentVo;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    CommentMapper CommentMapper;

    @Override
    public IPage<CommentVo> paging(Page page, Long postId, Long userId, String order) {
        return CommentMapper.selectComments(page,new QueryWrapper<Comment>()
                .eq(postId != null, "post_id",postId)
                .eq(userId != null, "user_id",userId)
                .orderByDesc(order != null, order));
    }
}
