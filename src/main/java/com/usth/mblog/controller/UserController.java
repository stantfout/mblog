package com.usth.mblog.controller;

import java.io.IOException;
import	java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.usth.mblog.common.lang.Result;
import com.usth.mblog.entity.Post;
import com.usth.mblog.entity.User;
import com.usth.mblog.entity.UserMessage;
import com.usth.mblog.shiro.AccountProfile;
import com.usth.mblog.util.UploadUtil;
import com.usth.mblog.vo.UserMessageVo;
import org.apache.shiro.SecurityUtils;
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

        /*
         * 更新完头像后会发/user/set的Post请求，需要单独判断更新用户
         */
        if (StrUtil.isNotBlank(user.getAvatar())) {
            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());

            AccountProfile profile = getProfile();
            profile.setAvatar(user.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile",profile);
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
        SecurityUtils.getSubject().getSession().setAttribute("profile",profile);

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

    @GetMapping("/user/index")
    public String index() {
        return "user/index";
    }

    @GetMapping("/user/public")
    @ResponseBody
    public Result userPublic() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));

        return Result.success(page);
    }

    @GetMapping("/user/collection")
    @ResponseBody
    public Result userCollection() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id","select post_id from user_collection where user_id = " + getProfileId()));

        return Result.success(page);
    }

    @GetMapping("/user/message")
    public String message() {

        IPage<UserMessageVo> page = messageService.paging(getPage(),new QueryWrapper<UserMessage>()
                .eq("to_user_id",getProfileId())
                .eq("status",0)
                .orderByAsc("created"));
        request.setAttribute("pageData",page);
        return "user/message";
    }

    @GetMapping("/user/{id:\\d*}")
    public String userHome(@PathVariable Long id) {
        User user = userService.getById(id);

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", user.getId())
                .gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created"));
        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        return "user/home";
    }

    @PostMapping("/message/remove")
    @ResponseBody
    public Result msgRemove(@RequestParam(defaultValue = "0") Long id,@RequestParam(defaultValue = "false") Boolean all) {
        boolean res = messageService.update(new UpdateWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id)
                .set("status", 1));
        return res ? Result.success() : Result.failed("删除失败");
    }

    @ResponseBody
    @RequestMapping("/message/nums")
    public Map msgNums() {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0"));
        return MapUtil.builder("status",0).put("count",count).build();

    }

}
