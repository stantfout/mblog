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

    PostVo selectOnePost(QueryWrapper<Post> wrapper);
}
