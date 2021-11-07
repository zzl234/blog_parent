package com.zzl.service;

import com.zzl.vo.Result;
import com.zzl.vo.params.CommentParam;

public interface CommentsService {

    /**
     * 根据文章id返回评论详情
     * @param articleId
     * @return
     */
    Result commentsByArticleId(Long articleId);

    Result comment(CommentParam commentParam);
}
