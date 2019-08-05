package com.youlexuan.search.service;

import com.youlexuan.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface SearchService {

    public Map<String,Object> search(Map searchMap);

    public  void importList(List<TbItem> itemList);

    void deleteSolrIndex(Long[] ids);
}
