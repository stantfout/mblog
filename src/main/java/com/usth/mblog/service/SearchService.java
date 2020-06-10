package com.usth.mblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.search.mq.PostMqIndexMessage;
import com.usth.mblog.vo.PostVo;

import java.util.List;

public interface SearchService {
    IPage search(Page page, String keyword);

    int initEsData(List<PostVo> records);

    void createOrUpdate(PostMqIndexMessage message);

    void removeIndex(PostMqIndexMessage message);
}
