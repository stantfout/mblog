package com.usth.mblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.usth.mblog.vo.CommentVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
public interface CommentService extends IService<Comment> {

    /**
     * @param page 分页
     * @param postId 文章id
     * @param userId 用户id
     * @param order 排序
     * @return
     */
    IPage<CommentVo> paging(Page page, Long postId, Long userId, String order);
}
