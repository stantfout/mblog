package com.usth.mblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Comment;
import com.usth.mblog.entity.Post;
import com.usth.mblog.mapper.CommentMapper;
import com.usth.mblog.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usth.mblog.service.PostService;
import com.usth.mblog.util.RedisKeyUtil;
import com.usth.mblog.util.RedisUtil;
import com.usth.mblog.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    PostService postService;

    @Override
    public IPage<CommentVo> paging(Page page, Long postId, Long userId, String order) {
        return CommentMapper.selectComments(page,new QueryWrapper<Comment>()
                .eq(postId != null, "post_id",postId)
                .eq(userId != null, "user_id",userId)
                .eq("c.status",0)
                .orderByAsc(order != null, order));
    }

    @Override
    public List<CommentVo> commentList(QueryWrapper<Comment> wrapper) {
        List<Comment> list = this.list(wrapper);
        List<CommentVo> comments = new ArrayList<>();
        for (Comment comment : list) {
            CommentVo vo = new CommentVo();
            vo.setId(comment.getId());
            vo.setContent(comment.getContent());
            vo.setCreated(comment.getCreated());

            String title;
            String postKey = RedisKeyUtil.getPostKey(comment.getPostId());
            if (redisUtil.hHasKey(postKey,RedisKeyUtil.getPostTitleKey())) {
                title = (String)redisUtil.hget(postKey, RedisKeyUtil.getPostTitleKey());
            } else {
                Post post = postService.getOne(new QueryWrapper<Post>()
                        .select("title")
                        .eq("id", comment.getPostId()));
                title = post.getTitle();
                redisUtil.hset(postKey,RedisKeyUtil.getPostTitleKey(),post.getTitle());
            }
            vo.setPostTitle(title);
            vo.setPostId(comment.getPostId());
            comments.add(vo);
        }
        return comments;
    }
}
