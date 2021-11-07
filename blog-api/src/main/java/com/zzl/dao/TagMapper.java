package com.zzl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.pojo.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章查询标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 查询最热标签前n条
     * @param limit
     * @return
     */
    List<Long> findHotsTagId(int limit);

    List<Tag> findTagsByTagIds(@Param("tagIds") List<Long> tagIds);
}
