package com.zzl.service;

import com.zzl.vo.Result;
import com.zzl.vo.TagVo;

import java.util.List;

public interface TagService {

    List<TagVo> findTagByArticleId(Long articleId);

    Result hots(int limit);

    /**
     * 查询所有的文章标签
     * @return
     */
    Result findALL();

    Result findAllDetail();

    Result findDetailById(Long id);
}
