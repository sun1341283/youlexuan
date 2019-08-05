package com.youlexuan.sellergoods.service;

import com.youlexuan.entity.PageResult;
import com.youlexuan.pojo.TbBrand;

import java.util.List;

public interface IBrandService {

    List<TbBrand> findAll();
    PageResult findPage(int pageNum,int pageSize);
}
