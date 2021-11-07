package com.zzl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.dao.ArticleBodyMapper;
import com.zzl.dao.ArticleMapper;
import com.zzl.dao.ArticleTagMapper;
import com.zzl.dos.Archives;
import com.zzl.pojo.Article;
import com.zzl.pojo.ArticleBody;
import com.zzl.pojo.ArticleTag;
import com.zzl.pojo.SysUser;
import com.zzl.service.*;
import com.zzl.utils.UserThreadLocal;
import com.zzl.vo.*;
import com.zzl.vo.params.ArticleParam;
import com.zzl.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public Result listArticles() {
        List<Archives> archivesList = articleMapper.listArticles();
        return Result.success(archivesList);
    }

    /**
     * 最新
     *
     * @param limit
     * @return
     */
    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Article::getCreateDate);
        wrapper.select(Article::getId, Article::getTitle);
        wrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(wrapper);
        return Result.success(copylist(articles, false, false));
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Article::getViewCounts);
        wrapper.select(Article::getId, Article::getTitle);
        wrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(wrapper);
        return Result.success(copylist(articles, false, false));
    }

    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return Result.success(copylist(records, true, true));
    }

    /*@Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        if (pageParams.getCategoryId() != null){
            wrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }

        // 标签文章列表显示
        List<Long> articleIdList = new ArrayList<>();
        if (pageParams.getTagId() != null){
            // 加入标签，条件查询
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId, pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }

            if (articleIdList.size() > 0){
                wrapper.in(Article::getId, articleIdList);
            }
        }

        //置顶排序
        //按创建时间排序
        wrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, wrapper);
        List<Article> records = articlePage.getRecords();

        List<ArticleVo> articlesVoList = copylist(records,true, true);
        return Result.success(articlesVoList);
    }*/

    private List<ArticleVo> copylist(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, false, false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copylist(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategoey) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategoey));
        }
        return articleVoList;
    }

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategoey) {
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategoey) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }


    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }

    @Override
    public Result findArticleById(Long articleId) {
        Article article = this.articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true, true, true);

        threadService.updateArticleViewCount(articleMapper, article);
        return Result.success(articleVo);
    }


    @Override
    public Result publish(ArticleParam articleParam) {
        // 获取作者信息
        SysUser user = UserThreadLocal.get();
        /**
         * 1.发布文章 构件Article对象
         * 2.文章作者信息
         * 3.标签 将标签加入到关联列表当中
         * 4.body 内容存储
         */
        Article article = new Article();
        article.setAuthorId(user.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParam.getSummary());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        // 插入文章后会生成一个id
        this.articleMapper.insert(article);
        // tags
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }

        // body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        Map<String, String> map = new HashMap<>();
        map.put("id", article.getId().toString());
        return Result.success(map);
    }
}
