package com.usth.mblog.service.impl;

import com.usth.mblog.entity.UserMessage;
import com.usth.mblog.mapper.UserMessageMapper;
import com.usth.mblog.service.UserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
