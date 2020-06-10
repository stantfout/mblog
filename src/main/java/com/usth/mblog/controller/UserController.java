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
import com.usth.mblog.entity.Comment;
import com.usth.mblog.entity.Post;
import com.usth.mblog.entity.User;
import com.usth.mblog.entity.UserMessage;
import com.usth.mblog.shiro.AccountProfile;
import com.usth.mblog.util.UploadUtil;
import com.usth.mblog.vo.CommentVo;
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

    /**
     * 用户首页
     * @return
     */
    @GetMapping("/user/home")
    public String home() {

        User user = userService.getById(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", user.getId())
                .eq("status",0)
                .gt("created", DateUtil.offsetDay(new Date(), -7))
                .orderByDesc("created"));
        List<CommentVo> comments = commentService.commentList(new QueryWrapper<Comment>()
                .eq("user_id", user.getId())
                .eq("status", 0)
                .gt("created", DateUtil.offsetDay(new Date(), -7))
                .orderByDesc("created"));

        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        request.setAttribute("comments", comments);
        return "user/home";
    }

    /**
     * 用户设置
     * @return
     */
    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(getProfileId());
        request.setAttribute("user",user);
        return "user/set";
    }

    /**
     * 修改用户设置
     * @param user
     * @return
     */
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


    /**
     * 上传头像
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/user/upload")
    @ResponseBody
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtils.upload(UploadUtil.type_avatar,file);
    }

    /**
     * 修改密码
     * @param nowpass
     * @param pass
     * @param repass
     * @return
     */
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

    /**
     * 用户首页
     * @return
     */
    @GetMapping("/user/index")
    public String index() {
        return "user/index";
    }

    /**
     * 用户发布的文章
     * @return
     */
    @GetMapping("/user/public")
    @ResponseBody
    public Result userPublic() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .eq("status",0)
                .orderByDesc("created"));

        return Result.success(page);
    }

    /**
     * 用户收藏
     * @return
     */
    @GetMapping("/user/collection")
    @ResponseBody
    public Result userCollection() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id","select post_id from user_collection where user_id = " + getProfileId())
                .eq("status",0));

        return Result.success(page);
    }

    /**
     * 用户收到的消息
     * @return
     */
    @GetMapping("/user/message")
    public String message() {

        IPage<UserMessageVo> page = messageService.paging(getPage(),new QueryWrapper<UserMessage>()
                .eq("to_user_id",getProfileId())
                .eq("status",0)
                .orderByDesc("created"));
        request.setAttribute("pageData",page);
        return "user/message";
    }

    /**
     * 查看用户信息
     * @param id
     * @return
     */
    @GetMapping("/user/{id:\\d*}")
    public String userHome(@PathVariable Long id) {
        User user = userService.getById(id);

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", user.getId())
                .eq("status",0)
                .gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created"));
        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        return "user/home";
    }

    /**
     * 删除消息
     * @param id
     * @param all
     * @return
     */
    @PostMapping("/message/remove")
    @ResponseBody
    public Result messageRemove(@RequestParam(defaultValue = "0") Long id,@RequestParam(defaultValue = "false") Boolean all) {
        boolean res = messageService.update(new UpdateWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id)
                .set("status", 1));
        return res ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 未读消息数量
     * @return
     */
    @ResponseBody
    @RequestMapping("/message/nums")
    public Map messageNums() {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0"));
        return MapUtil.builder("status",0).put("count",count).build();

    }

}
