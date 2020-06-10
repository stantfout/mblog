package com.usth.mblog.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.usth.mblog.common.lang.Result;
import com.usth.mblog.config.RabbitConfig;
import com.usth.mblog.entity.*;
import com.usth.mblog.search.mq.PostMqIndexMessage;
import com.usth.mblog.util.ValidationUtil;
import com.usth.mblog.vo.CommentVo;
import com.usth.mblog.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Controller
public class PostController extends BaseController {

    /**
     * 分类信息
     * @param id 分类Id
     * @param order 排序规则(默认按照时间排序)
     * @param recommend 是否只选择精华文章
     * @return
     */
    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable Long id,
                           @RequestParam(defaultValue = "created")String order,
                           @RequestParam(defaultValue = "0")Integer recommend) {
        Boolean flag = recommend == 1 ? true : null;
        //设置当前页面的分类id，用于前端高亮显示
        request.setAttribute("currentCategoryId",id);

        //根据分类获取分页的文章信息
        IPage<PostVo> results = postService.paging(getPage(),id,null,null,flag,order);

        request.setAttribute("pageData",results);
        request.setAttribute("order",order);
        request.setAttribute("recommend",recommend);
        return "post/category";
    }

    /**
     * 文章详细信息
     * @param id
     * @return
     */
    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable Long id) {
        //根据文章Id获取文章信息
        PostVo vo = postService.selectOnePost(new QueryWrapper<Post>()
                .eq("p.id",id)
                .eq("p.status",0));
        Assert.notNull(vo,"文章已经被删除");
        postService.putViewCount(vo);

        //获取分页的评论信息
        IPage<CommentVo> result = commentService.paging(getPage(),vo.getId(),null,"created");

        request.setAttribute("currentCategoryId",vo.getCategoryId());
        request.setAttribute("post",vo);
        request.setAttribute("pageData",result);
        return "post/detail";
    }

    /**
     * 判断用户是否收藏了文章
     * @param pid
     * @return
     */
    @PostMapping("/collection/find/")
    @ResponseBody
    public Result collectionFind(Long pid) {
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));
        return Result.success(MapUtil.of("collection", count > 0));
    }

    /**
     * 添加收藏
     * @param pid
     * @return
     */
    @PostMapping("/collection/add/")
    @ResponseBody
    public Result addCollection(Long pid){
        Post post = postService.getOne(new QueryWrapper<Post>().select("user_id")
                .eq("id", pid)
                .eq("status",0));
        Assert.notNull(post,"该帖子已被删除");
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));
        if(count > 0) {
            return Result.failed("你已经收藏了");
        }
        UserCollection col = new UserCollection();
        col.setUserId(getProfileId());
        col.setPostId(pid);
        col.setCreated(new Date());
        col.setModified(new Date());
        col.setPostUserId(post.getUserId());
        collectionService.save(col);
        return Result.success();
    }

    /**
     * 取消收藏
     * @param pid
     * @return
     */
    @PostMapping("/collection/remove/")
    @ResponseBody
    public Result removeCollection(Long pid){
        Post post = postService.getOne(new QueryWrapper<Post>().select("user_id")
                .eq("id", pid)
                .eq("status",0));
        Assert.notNull(post,"该帖子已被删除");

        collectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        return Result.success();
    }

    /**
     * 编辑文章
     * @param id
     * @return
     */
    @GetMapping("/post/edit")
    public String edit(@RequestParam(required = false) Long id) {
        if (id != null) {
            Post post = postService.getOne(new QueryWrapper<Post>()
                    .eq("id", id)
                    .eq("status",0));
            Assert.notNull(post,"该帖子已被删除");
            Assert.isTrue(post.getUserId().equals(getProfileId()),"没有权限操作此文章");
            request.setAttribute("post",post);
        }
        request.setAttribute("categories",categoryService.list());
        return "post/edit";
    }

    /**
     * 创建文章
     * @param post
     * @param vercode 验证码
     * @return
     */
    @PostMapping("/post/submit")
    @ResponseBody
    public Result submitPost(Post post, String vercode) {
        //参数校验
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if (validResult.hasErrors()) {
            return Result.failed(validResult.getErrors());
        }
        String captcha = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        if(vercode == null || !vercode.equalsIgnoreCase(captcha)) {
            return Result.failed("验证码错误");
        }

        if (post.getId() == null) {
            post.setUserId(getProfileId());
            post.setCreated(new Date());
            post.setModified(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            post.setStatus(0);

            postService.save(post);
            log.info(post.toString());
        } else {
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().equals(getProfileId()), "没有权限操作此文章");

            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());
            postService.updateById(tempPost);
        }

        //发送消息给MQ，告知更新或添加
        amqpTemplate.convertAndSend(RabbitConfig.es_exchange, RabbitConfig.es_bind_key,new PostMqIndexMessage(post.getId(),PostMqIndexMessage.CREATE_OR_UPDATE));

        return Result.success().action("/post/"+post.getId());
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @PostMapping("/post/delete")
    @ResponseBody
    @Transactional
    public Result deletePost(Long id){
        Post post = postService.getOne(new QueryWrapper<Post>()
                .eq("id", id)
                .eq("status",0));

        Assert.notNull(post,"该帖子已经被删除");
        Assert.isTrue(post.getUserId().equals(getProfileId()),"没有权限删除此文章");

        postService.update(new UpdateWrapper<Post>()
                .eq("id",id)
                .set("status",1));

        messageService.update(new UpdateWrapper<UserMessage>()
                .eq("post_id",id)
                .set("status",1));

        //发送消息给MQ，告知删除
        amqpTemplate.convertAndSend(RabbitConfig.es_exchange, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(),PostMqIndexMessage.REMOVE));


        return Result.success().action("/user/index");
    }

    /**
     * 回复文章
     * @param pid
     * @param content
     * @return
     */
    @PostMapping("/post/reply")
    @ResponseBody
    public Result reply(@RequestParam("jid") Long pid, String content) {
        Assert.notNull(pid,"找不到对应文章");
        Assert.notNull(content,"评论不能为空");

        Post post = postService.getOne(new QueryWrapper<Post>()
                .eq("id",pid)
                .eq("status",0));
        Assert.notNull(post,"该文章已经被删除");

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(pid);
        comment.setUserId(getProfileId());
        comment.setVoteUp(0);
        comment.setVoteDown(0);
        comment.setLevel(0);
        comment.setStatus(0);
        comment.setCreated(new Date());
        comment.setModified(new Date());
        commentService.save(comment);

        //数据库评论数量加一
        int count = commentService.count(new QueryWrapper<Comment>()
                .eq("status", 0)
                .eq("post_id", pid));
        postService.update(new UpdateWrapper<Post>()
                .eq("id",pid)
                .set("comment_count",count));

        //本周热议加一
        postService.incrCommentCountAndUnionForWeekRank(post.getId(),true);

        //发送消息
        User user = null;
        if (content.startsWith("@")) {
            String username = content.substring(1,content.indexOf(" "));
            log.info(username);
            user = userService.getOne(new QueryWrapper<User>()
                    .eq("username", username));
        }
        UserMessage message = new UserMessage();
        message.setFromUserId(getProfileId());
        if (user != null) {
            //发给评论
            message.setToUserId(user.getId());
            message.setType(2);
        } else {
            //发给题主
            message.setToUserId(post.getUserId());
            message.setType(1);
        }
        message.setPostId(pid);
        message.setPostId(post.getId());
        message.setCommentId(comment.getId());
        message.setContent(content);
        message.setCreated(new Date());
        message.setModified(new Date());
        message.setStatus(0);
        messageService.save(message);

        //即时通知作者
        wsService.sendMessageCountToUser(message.getToUserId());

        return Result.success().action("/post/" + pid);
    }

}
