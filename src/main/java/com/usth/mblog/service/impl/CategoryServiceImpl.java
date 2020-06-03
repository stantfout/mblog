package com.usth.mblog.service.impl;

import com.usth.mblog.entity.Category;
import com.usth.mblog.mapper.CategoryMapper;
import com.usth.mblog.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
