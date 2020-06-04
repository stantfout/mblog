package com.usth.mblog.template;

import com.usth.mblog.common.templates.DirectiveHandler;
import com.usth.mblog.common.templates.TemplateDirective;
import com.usth.mblog.util.RedisKeyUtil;
import com.usth.mblog.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 本周热议
 */
@Component
public class HotsTemplate extends TemplateDirective {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String weekRankKey = RedisKeyUtil.getWeekRankKey();

        Set<ZSetOperations.TypedTuple> typedTuples = redisUtil.getZSetRank(weekRankKey, 0, 7);

        List<Map> hotPosts = new ArrayList<>();

        for (ZSetOperations.TypedTuple typedTuple : typedTuples) {
            Map<String, Object> map = new HashMap<>();

            Object value = typedTuple.getValue(); // post的id
            String postKey = RedisKeyUtil.getPostKey(value);

            map.put("id", value);
            map.put("title", redisUtil.hget(postKey, RedisKeyUtil.getPostTitleKey()));
            map.put("commentCount", typedTuple.getScore());

            hotPosts.add(map);
        }

        handler.put(RESULTS, hotPosts).render();

    }
}
