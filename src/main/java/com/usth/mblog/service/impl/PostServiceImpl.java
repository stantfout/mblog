package com.usth.mblog.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Post;
import com.usth.mblog.mapper.PostMapper;
import com.usth.mblog.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usth.mblog.util.RedisKeyUtil;
import com.usth.mblog.util.RedisUtil;
import com.usth.mblog.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    PostMapper postMapper;

    @Autowired
    RedisUtil redisUtil;

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

    @Override
    public void initWeekRank() {

        //获取7天的文章
        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.lastWeek())
                .select("id","title","comment_count","view_count","user_id","created"));
        //初始化文章的总评论数
        for (Post post : posts) {
            String key = RedisKeyUtil.getDayRankKey(DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT));
            redisUtil.zSet(key,post.getId(),post.getCommentCount());
            //7天后自动过期
            long expireTime = (7 - DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY)) * 24 * 60 * 60;
            redisUtil.expire(key,expireTime);
            // 缓存文章的一些基本信息
            this.hashCachePostIdAndTitle(post,expireTime);
        }
        //做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }


    @Override
    public void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        //修改今天和本周的redis评论数量
        String dayRankKey = RedisKeyUtil.getDayRankKey(DateUtil.format(new Date(),DatePattern.PURE_DATE_FORMAT));
        String weekRankKey = RedisKeyUtil.getWeekRankKey();
        redisUtil.zIncrementScore(dayRankKey,postId, isIncr ? 1 : -1);
        redisUtil.zIncrementScore(weekRankKey,postId, isIncr ? 1 : -1);

        //如果redis没有缓存这篇文章的信息，缓存文章信息
        String key = RedisKeyUtil.getPostKey(postId);
        if (!redisUtil.hasKey(key)) {
            long expireTime = 7 * 24 * 60 * 60;
            Post post = this.getById(postId);
            redisUtil.hset(key,RedisKeyUtil.getPostIdKey(),post.getId(), expireTime);
            redisUtil.hset(key,RedisKeyUtil.getPostTitleKey(),post.getTitle(), expireTime);
            redisUtil.hset(key,RedisKeyUtil.getPostViewCountKey(),post.getViewCount(), expireTime);
        }

    }

    @Override
    public void putViewCount(PostVo vo) {
        String postKey = RedisKeyUtil.getPostKey(vo.getId());
        //1. 从缓存中获取viewCount
        Integer viewCount = (Integer) redisUtil.hget(postKey, RedisKeyUtil.getPostViewCountKey());
        if (viewCount != null) {
            vo.setViewCount(viewCount + 1);
        } else {
            //2. 如果没有，就从实体里面获取后加一
            vo.setViewCount(vo.getViewCount() + 1);
        }
        //3. 同步到缓存中
        redisUtil.hset(postKey,RedisKeyUtil.getPostViewCountKey(),vo.getViewCount());
    }

    @Override
    public void updateWeekRank() {
        this.zunionAndStoreLast7DayForWeekRank();
    }

    /**
     * 本周合并评论数量操作
     */
    private void zunionAndStoreLast7DayForWeekRank() {
        String currentKey = RedisKeyUtil.getDayRankKey(DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT));

        String key = RedisKeyUtil.getWeekRankKey();
        List<String> otherKeys = new ArrayList<>();
        for (int i = -6; i < 0; i++) {
            String temp = RedisKeyUtil.getDayRankKey(
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT));
            otherKeys.add(temp);
        }

        redisUtil.zUnionAndStore(currentKey,otherKeys,key);
    }

    /**
     * 缓存文章的基本信息
     * @param post
     * @param expireTime
     */
    private void hashCachePostIdAndTitle(Post post, long expireTime) {
        String key = RedisKeyUtil.getPostKey(post.getId());
        if (!redisUtil.hasKey(key)) {
            redisUtil.hset(key,RedisKeyUtil.getPostIdKey(),post.getId(), expireTime);
            redisUtil.hset(key,RedisKeyUtil.getPostTitleKey(),post.getTitle(), expireTime);
            redisUtil.hset(key,RedisKeyUtil.getPostViewCountKey(),post.getViewCount(), expireTime);
        }
    }
}
