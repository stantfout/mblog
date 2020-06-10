package com.usth.mblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.search.mq.PostMqIndexMessage;
import com.usth.mblog.vo.PostVo;

import java.util.List;

public interface SearchService {

    /**
     * 搜索查询
     * @param page
     * @param keyword
     * @return
     */
    IPage search(Page page, String keyword);

    /**
     * 初始化Es数据
     * @param records
     * @return
     */
    int initEsData(List<PostVo> records);

    /**
     * 更新或者创建Es数据
     * @param message
     */
    void createOrUpdate(PostMqIndexMessage message);

    /**
     * 删除Es数据
     * @param message
     */
    void removeIndex(PostMqIndexMessage message);
}
