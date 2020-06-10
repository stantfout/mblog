package com.usth.mblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.common.lang.Result;
import com.usth.mblog.config.RabbitConfig;
import com.usth.mblog.entity.Comment;
import com.usth.mblog.entity.Post;
import com.usth.mblog.entity.UserMessage;
import com.usth.mblog.search.mq.PostMqIndexMessage;
import com.usth.mblog.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    /**
     * 管理员文章操作
     * @param id 文章Id
     * @param rank 操作对应状态(取消置顶，加精/置顶，加精)
     * @param field 操作名称
     * @return
     */
    @PostMapping("/jie-set")
    @ResponseBody
    public Result jetSet(Long id, Integer rank, String field) {
        Assert.notNull(id,"找不到对应文章");
        Post post = postService.getOne(new QueryWrapper<Post>()
                .eq("id",id)
                .eq("status",0));
        Assert.notNull(post,"该文章已被删除了");
        if (field.equals("delete")) {
            //删除
            deletePost(post);
        } else if (field.equals("stick")) {
            //置顶
            stickPost(post,rank);
        } else if(field.equals("status")) {
            //加精
            statusPost(post,rank);
        } else {
            return Result.failed("操作不支持");
        }

        return Result.success();
    }

    @PostMapping("/accept")
    @ResponseBody
    public Result accept(Long id){

        //判断评论是否存在
        Assert.notNull(id,"找不到对应评论");
        Comment comment = commentService.getById(id);
        Assert.isTrue(comment.getStatus() == 0,"评论已经被删除");

        //修改level等级,更新评论
        comment.setLevel(1);
        commentService.updateById(comment);

        //发送消息
        UserMessage message = new UserMessage();
        message.setFromUserId((long) 0);
        message.setToUserId(comment.getUserId());
        message.setPostId(comment.getPostId());
        message.setContent("您的评论：\"" + comment.getContent() + "\"被采纳了");
        message.setType(0);
        message.setCreated(new Date());
        message.setModified(new Date());
        message.setStatus(0);
        messageService.save(message);

        //及时通知作者
        wsService.sendMessageCountToUser(message.getToUserId());

        return Result.success();
    }

    @PostMapping("/delete")
    @ResponseBody
    @Transactional
    public Result deleteComment(Long id) {

        //判断评论是否存在
        Assert.notNull(id,"找不到对应评论");
        Comment comment = commentService.getById(id);
        Assert.isTrue(comment.getStatus() == 0,"评论已经被删除");

        //删除评论
        comment.setStatus(1);
        commentService.updateById(comment);
        messageService.update(new UpdateWrapper<UserMessage>()
                .eq("comment_id",id)
                .set("status",1));

        //评论数量减一
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.updateById(post);
        postService.incrCommentCountAndUnionForWeekRank(comment.getPostId(),false);

        //发送消息
        UserMessage message = new UserMessage();
        message.setFromUserId((long) 0);
        message.setToUserId(comment.getUserId());
        message.setPostId(comment.getPostId());
        message.setContent("您的评论：\"" + comment.getContent() + "\"被删除了");
        message.setType(0);
        message.setCreated(new Date());
        message.setModified(new Date());
        message.setStatus(0);
        messageService.save(message);

        //及时通知作者
        wsService.sendMessageCountToUser(message.getToUserId());


        return Result.success();
    }

    private Result deletePost(Post post) {

        postService.update(new UpdateWrapper<Post>()
                .eq("id",post.getId())
                .set("status",1));

        messageService.update(new UpdateWrapper<UserMessage>()
                .eq("post_id",post.getId())
                .set("status",1));

        //发送消息给MQ，告知删除
        amqpTemplate.convertAndSend(RabbitConfig.es_exchange, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(),PostMqIndexMessage.REMOVE));

        return Result.success();
    }

    private Result stickPost(Post post, Integer rank) {
        post.setLevel(rank);
        postService.updateById(post);
        return Result.success();
    }

    private Result statusPost(Post post, Integer rank) {
        post.setRecommend(rank == 1);
        postService.updateById(post);
        return Result.success();
    }

    @PostMapping("/initEsData")
    @ResponseBody
    public Result initEsData() {

        int size = 10000;
        Page page = new Page<>();
        page.setSize(size);

        long total = 0;

        for (int i = 1; i < 1000; i++) {
            page.setCurrent(i);

            IPage<PostVo> paging = postService.paging(page, null, null, null, null, null);

            int num = searchService.initEsData(paging.getRecords());

            total += num;

            if (paging.getRecords().size() < size) {
                break;
            }
        }

        return Result.success("ES索引初始化成功，共" + total + "条");
    }

}
