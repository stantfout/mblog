package com.usth.mblog.shiro;

import com.usth.mblog.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    /**
     * 获取用户的角色和权限等信息
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 身份验证，验证用户的合法性
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken UsernamePasswordToken = (UsernamePasswordToken) token;
        AccountProfile profile = userService.login(UsernamePasswordToken.getUsername(), String.valueOf(UsernamePasswordToken.getPassword()));

        SecurityUtils.getSubject().getSession().setAttribute("profile",profile);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(profile,token.getCredentials(),getName());
        return info;
    }
}
