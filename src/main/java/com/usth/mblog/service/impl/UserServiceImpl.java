package com.usth.mblog.service.impl;

import com.usth.mblog.entity.User;
import com.usth.mblog.mapper.UserMapper;
import com.usth.mblog.service.UserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
