package com.usth.mblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usth.mblog.vo.PostVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 获取分页的文章信息
     * @param page
     * @param wrapper
     * @return
     */
    IPage<PostVo> selectPosts(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);

    /**
     * 获取单个文章信息
     * @param wrapper
     * @return
     */
    PostVo selectOnePost(@Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);
}
