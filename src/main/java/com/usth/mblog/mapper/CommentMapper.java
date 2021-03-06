package com.usth.mblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usth.mblog.vo.CommentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
@Component
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 获取分页的评论信息
     * @param page 分页信息
     * @param wrapper 查询规则
     * @return
     */
    IPage<CommentVo> selectComments(Page page, @Param(Constants.WRAPPER) QueryWrapper<Comment> wrapper);
}
