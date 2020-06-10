package com.usth.mblog.service.impl;
import	java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usth.mblog.entity.Post;
import com.usth.mblog.search.model.PostDocument;
import com.usth.mblog.search.mq.PostMqIndexMessage;
import com.usth.mblog.search.repository.PostRepository;
import com.usth.mblog.service.PostService;
import com.usth.mblog.service.SearchService;
import com.usth.mblog.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PostService postService;

    @Override
    public IPage search(Page page, String keyword) {

        // 分页信息 mybatis-plus的page 转成 jap的page
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
        Pageable pageable = PageRequest.of(current.intValue(),size.intValue());

        // 搜索es得到page
        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery(keyword, "title", "authorName","categoryName");
        org.springframework.data.domain.Page<PostDocument> documents = postRepository.search(builder, pageable);

        // 结果信息 jap的page 转成 mybatis-plus的page
        IPage pageData = new Page(page.getCurrent(), page.getSize(), documents.getTotalElements());
        pageData.setRecords(documents.getContent());

        return pageData;
    }

    @Override
    public int initEsData(List<PostVo> records) {

        if (records == null || records.size() == 0) {
            return 0;
        }

        List<PostDocument> documents = new ArrayList<> ();

        for (PostVo record : records) {
            PostDocument document = modelMapper.map(record, PostDocument.class);
            documents.add(document);
        }

        postRepository.saveAll(documents);

        return documents.size();
    }

    @Override
    public void createOrUpdate(PostMqIndexMessage message) {
        Long postId = message.getPostId();
        PostVo postVo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id",postId));

        PostDocument document = modelMapper.map(postVo, PostDocument.class);

        postRepository.save(document);
        log.info("es索引更新成功 -----> {}",document.toString());
    }

    @Override
    public void removeIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        postRepository.deleteById(postId);
        log.info("es索引删除成功 -----> {}",message.toString());

    }
}
