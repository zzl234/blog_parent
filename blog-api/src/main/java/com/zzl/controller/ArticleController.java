package com.zzl.controller;

import com.zzl.common.aop.LogAnnotation;
import com.zzl.common.cache.Cache;
import com.zzl.service.ArticleService;
import com.zzl.vo.Result;
import com.zzl.vo.params.ArticleParam;
import com.zzl.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 首页 文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    // 代表对此接口进行日志记录
    @LogAnnotation(module="文章", operator = "获取文章列表")
    public Result listArticle(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }


    /**
     * 首页最热文章
     * @return
     */
    @PostMapping("hot")
    @Cache(expire = 1*60*1000, name = "hot_article")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 首页最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire = 1*60*1000, name = "new_article")
    public Result newArticles(){
        int limit = 5;
        return articleService.newArticles(limit);
    }

    @PostMapping("listArchives")
    @Cache(expire = 1*60*1000, name = "listArticles")
    public Result listArticles(){
        return articleService.listArticles();
    }


    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam, HttpServletResponse response){
        return articleService.publish(articleParam);
    }

}
