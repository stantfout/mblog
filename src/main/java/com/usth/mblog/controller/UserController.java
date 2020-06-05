package com.usth.mblog.controller;

import java.io.IOException;
import	java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usth.mblog.common.lang.Result;
import com.usth.mblog.entity.Post;
import com.usth.mblog.entity.User;
import com.usth.mblog.shiro.AccountProfile;
import com.usth.mblog.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController extends BaseController{

    @Autowired
    UploadUtil uploadUtils;

    @GetMapping("/user/home")
    public String home() {

        User user = userService.getById(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", user.getId())
                .gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created"));
        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        return "user/home";
    }

    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(getProfileId());
        request.setAttribute("user",user);
        return "user/set";
    }

    @PostMapping("/user/set")
    @ResponseBody
    public Result doSet(User user) {

        if (StrUtil.isNotBlank(user.getAvatar())) {
            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());

            AccountProfile profile = getProfile();
            profile.setAvatar(user.getAvatar());

            return Result.success().action("user/set#avatar");
        }

        if (StrUtil.isBlank(user.getUsername())) {
            return Result.failed("昵称不能为空");
        }
        int count = userService.count(new QueryWrapper<User>()
                .eq("username", getProfile().getUsername())
                .ne("id", getProfileId()));
        if (count > 0) {
            return Result.failed("该昵称已经被占用");
        }
        User temp = userService.getById(getProfileId());
        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());

        return Result.success().action("user/set#info");
    }


    @PostMapping("/user/upload")
    @ResponseBody
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtils.upload(UploadUtil.type_avatar,file);
    }

    @PostMapping("/user/repass")
    @ResponseBody
    public Result repass(String nowpass, String pass, String repass) {
        if (!pass.equals(repass)) {
            return Result.failed("两次密码不同");
        }

        User user = userService.getById(getProfileId());

        if (!user.getPassword().equals(SecureUtil.md5(nowpass))) {
            return Result.failed("密码不正确");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        return Result.success().action("/user/set#pass");
    }


}
