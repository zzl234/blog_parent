package com.zzl.service;

import com.zzl.vo.Result;
import com.zzl.vo.params.ArticleParam;
import com.zzl.vo.params.PageParams;

public interface ArticleService {

    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 最新文章
     * @param limit
     * @return
     */
    Result newArticles(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArticles();


    /**
     * 查找展示文章
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);

    /**
     * 文章发布
     * @param articleParam
     * @return
     */
    Result publish(ArticleParam articleParam);
}
