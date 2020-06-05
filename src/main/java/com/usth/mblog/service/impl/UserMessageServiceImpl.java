package com.usth.mblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.UserMessage;
import com.usth.mblog.mapper.UserMessageMapper;
import com.usth.mblog.service.UserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usth.mblog.vo.UserMessageVo;
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
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Autowired
    UserMessageMapper messageMapper;

    @Override
    public IPage<UserMessageVo> paging(Page page, QueryWrapper<UserMessage> wrapper) {
        return messageMapper.selectMessages(page,wrapper);
    }
}
