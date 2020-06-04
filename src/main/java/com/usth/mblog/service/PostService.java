package com.usth.mblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.usth.mblog.vo.PostVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
public interface PostService extends IService<Post> {

    /**
     *
     * @param page 分页信息
     * @param categoryId 分类
     * @param userId 用户Id
     * @param level 置顶
     * @param recommend 精选
     * @param created 排序
     * @return
     */
    IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String created);

    /**
     * 根据条件获取单个页面
     * @param wrapper 条件
     * @return
     */
    PostVo selectOnePost(QueryWrapper<Post> wrapper);

    /**
     * 本周热议初始化
     */
    void initWeekRank();

    /**
     * 评论文章后的操作
     * @param postId 文章Id
     * @param isIncr 增加/减少评论
     */
    void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr);

    /**
     * 设置文章浏览量
     * @param vo
     */
    void putViewCount(PostVo vo);

    void updateWeekRank();
}
