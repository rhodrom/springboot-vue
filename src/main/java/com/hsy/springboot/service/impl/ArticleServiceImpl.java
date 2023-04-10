package com.hsy.springboot.service.impl;

import com.hsy.springboot.entity.Article;
import com.hsy.springboot.mapper.ArticleMapper;
import com.hsy.springboot.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hsy
 * @since 2023-04-09
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

}
