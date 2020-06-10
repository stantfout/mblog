package com.usth.mblog.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.code.kaptcha.Producer;
import com.usth.mblog.common.lang.Result;
import com.usth.mblog.entity.User;
import com.usth.mblog.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class AuthController extends BaseController {

    @Autowired
    Producer producer;

    /**
     * 图片验证码
     * @param response
     * @throws IOException
     */
    @GetMapping("/captcha.jpg")
    public void kaptcha(HttpServletResponse response) throws IOException {

        //验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        request.getSession().setAttribute(KAPTCHA_SESSION_KEY,text);

        response.setHeader("Cache-Control","no-store,no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image,"jpg",outputStream);
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public Result doLogin(String email, String password, String vercode) {
        if (StrUtil.isEmpty(email) || StrUtil.isBlank(password)) {
            return Result.failed("邮箱和密码不能为空");
        }
        String captcha = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        if(vercode == null || !vercode.equalsIgnoreCase(captcha)) {
            return Result.failed("验证码错误");
        }

        UsernamePasswordToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException) {
                return Result.failed("用户不存在");
            } else if (e instanceof LockedAccountException) {
                return Result.failed("用户被禁用");
            } else if (e instanceof IncorrectCredentialsException) {
                return Result.failed("密码错误");
            } else {
                return Result.failed("用户认证失败");
            }
        }
        return Result.success().action("/");
    }

    @GetMapping("/register")
    public String register() {

        return "auth/reg";
    }

    @PostMapping("/register")
    @ResponseBody
    public Result doRegister(User user, String repass, String vercode) {
        //参数校验
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if (validResult.hasErrors()) {
            return Result.failed(validResult.getErrors());
        }

        if(!user.getPassword().equals(repass)) {
            return Result.failed("两次输入的密码不相同");
        }

        String captcha = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        if(vercode == null || !vercode.equalsIgnoreCase(captcha)) {
            return Result.failed("验证码错误");
        }

        //完成注册
        Result result = userService.register(user);
        return result.action("/login");
    }

    @RequestMapping("/user/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }
}
