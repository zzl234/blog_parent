package com.zzl.service;

import com.zzl.vo.CategoryVo;
import com.zzl.vo.Result;

public interface CategoryService {

    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);
}
