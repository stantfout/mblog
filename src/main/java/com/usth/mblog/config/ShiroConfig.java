package com.usth.mblog.config;

import cn.hutool.core.map.MapUtil;
import com.usth.mblog.shiro.AccountRealm;
import com.usth.mblog.shiro.AuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm){

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);

        log.info("------------------>securityManager注入成功");

        return securityManager;
    }

    /**
     * 拦截路径配置
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置登录的url和登录成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
        // 配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        filterFactoryBean.setFilters(MapUtil.of("auth",authFilter()));

        Map<String, String> hashMap = new LinkedHashMap<>();
        //用户中心相关
        hashMap.put("/user/home","auth");
        hashMap.put("/user/set","auth");
        hashMap.put("/user/upload","auth");
        hashMap.put("/user/message","auth");
        hashMap.put("/user/index","auth");
        //收藏相关
        hashMap.put("/collection/find/","auth");
        hashMap.put("/collection/add/","auth");
        hashMap.put("/collection/remove/","auth");
        //发布编辑
        hashMap.put("/post/reply","auth");
        //登录
        hashMap.put("/login", "anon");
        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;

    }

    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }

}
