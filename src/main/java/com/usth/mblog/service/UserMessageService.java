package com.usth.mblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.usth.mblog.vo.UserMessageVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
public interface UserMessageService extends IService<UserMessage> {

    /**
     * 分页查询用户消息
     * @param page
     * @param wrapper
     * @return
     */
    IPage<UserMessageVo> paging(Page page, QueryWrapper<UserMessage> wrapper);
}
