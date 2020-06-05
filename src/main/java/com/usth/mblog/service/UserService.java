package com.usth.mblog.service;

import com.usth.mblog.common.lang.Result;
import com.usth.mblog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.usth.mblog.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
public interface UserService extends IService<User> {

    Result register(User user);

    AccountProfile login(String email, String password);
}
