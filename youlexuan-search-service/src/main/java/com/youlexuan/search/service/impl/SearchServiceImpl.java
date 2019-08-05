package com.youlexuan.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.StringUtils;

import java.util.*;

@Service(timeout = 500000)
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 0、根据关键字搜索 searchMap.get("keywords")
     * 1、根据关键字得到对应的所有的分类
     * 2、根据分类名称查询出 该分类下的品牌列表和规格列表
     * 3、到第一步 根据过滤条件，过滤搜索结果
     *      到第一步 根据过滤条件 价格，得到结果
     * @param searchMap
     * @return
     */

    @Override
    public Map<String, Object> search(Map searchMap) {

        Map resultMap = new HashMap();

        //1、得到高亮列表
        Map map = searchList(searchMap);
        resultMap.putAll(map);

        //2、根据关键字查询对应的所有分类
        List categoryList = searchCategoryList(searchMap);
        resultMap.put("catgoryList",categoryList);

        //3\
        if("".equals(searchMap.get("category"))&&categoryList.size()>0){
            Map brandAndSpecMap = searchBrandAndSpecList((String) categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
        }else{
            Map brandAndSpecMap = searchBrandAndSpecList((String) searchMap.get("category"));
        
            resultMap.putAll(brandAndSpecMap);
        }




        return resultMap;
    }

    @Override
    public void importList(List<TbItem> itemList) {
        for(TbItem tbItem:itemList){
            Map jsonObject = JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSpecMap(jsonObject);
            System.out.println(tbItem.getTitle());
        }
        System.out.println(itemList.size());
        solrTemplate.saveBeans(itemList);
        System.out.println("审核后新增索引");
        solrTemplate.commit();
    }

    @Override
    public void deleteSolrIndex(Long[] ids) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
        System.out.println("删除了ids为"+ids+"的索引");
    }


    /**
     * 得到高亮列表
     * @param searchMap
     * @return
     */

    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        // 根据关键字搜索
        String keywords = (String)searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        HighlightQuery query  = new SimpleHighlightQuery();
        //1.1 加工高亮选项，加工指定高亮的字段、高亮部分的前缀和后缀
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        /*HighlightOptions priceHighlight = new HighlightOptions().addField("item_price");
        priceHighlight.setSimplePrefix("<em style='color:red'>");
        priceHighlight.setSimplePostfix("</em>");
        query.setHighlightOptions(priceHighlight);*/

        //1.2加工查询条件
        Criteria criter = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criter);

        //1.3 加工分类筛选条件
        if(!"".equals(searchMap.get("category"))){

            Criteria filtercriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery fq = new SimpleFilterQuery(filtercriteria);
            query.addCriteria(filtercriteria);
            query.addFilterQuery(fq);
        }

        //1.4 加工品牌筛选条件
        if(!"".equals(searchMap.get("brand"))){

            Criteria filtercriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery fq = new SimpleFilterQuery(filtercriteria);
            query.addCriteria(filtercriteria);
            query.addFilterQuery(fq);
        }

        //1.4 加工spec筛选条件
        if(searchMap.get("spec")!=null&&((Map)searchMap.get("spec")).size()>0){
            Map<String,String> spcMap = (Map) searchMap.get("spec");
            for(String key:spcMap.keySet()){
                String specValue = spcMap.get(key);
                Criteria filtercriteria = new Criteria("item_spec_"+key).is(specValue);
                FilterQuery fq = new SimpleFilterQuery(filtercriteria);
                query.addCriteria(filtercriteria);
                query.addFilterQuery(fq);
            }
        }

        //1、4 加工价格过滤条件
        if(!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!price[0].equals("0")){
                Criteria filterCriter = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriter);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){
                Criteria filterCriter = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriter);
                query.addFilterQuery(filterQuery);
            }

        }

        //1.5 分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null) pageNo=1;
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null) pageSize = 10;

        query.setRows(pageSize);
        Integer offset = (pageNo-1)*pageSize;//偏移量、从第几条记录开始查询
        query.setOffset(offset);

        //1.6 排序的操作
        String sortValue =   (String)searchMap.get("sort");//asc,desc
        String sortField= (String) searchMap.get("sortField");//排序字段
        if(!StringUtils.isEmpty(sortValue)){
            Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
            if ("DESC".equals(sortValue)){
               sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
            }
            query.addSort(sort);
        }


        // 分析查询结果
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //1.3将加工好的高亮部分替换给,page.getContent
        //得到高亮集合列表
        for(HighlightEntry<TbItem> he:page.getHighlighted()){
            TbItem item = he.getEntity();
            if(he.getHighlights().size()>0&&he.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(he.getHighlights().get(0).getSnipplets().get(0));//替换实体的title为带着高亮显示的html标签
            }

        };

        List<TbItem> list = page.getContent();

        /*setValues(list);*/

       map.put("rows",list);
       map.put("totalPage",page.getTotalPages());//总页数
       map.put("total",page.getTotalElements());//分页总记录数
        return map;
    }
    /**
     * 传入list，通过id给每个item赋值
     */
    private void setValues(List<TbItem> list){
        for (TbItem item:list
             ) {
            TbItem itemInRedis = (TbItem)redisTemplate.boundHashOps("item").get(item.getId());
            item.setPrice(itemInRedis.getPrice());
            item.setImage(itemInRedis.getImage());
            item.setGoodsId(itemInRedis.getGoodsId());
        }
    }

    /**
     * 根据关键字查出所有的分类信息
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {

        List list = new ArrayList();
        Query query = new SimpleQuery();
        // 根据关键字查询
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Criteria criter = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criter);
        //根据item_category分组
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分组结果集
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> page = groupResult.getGroupEntries();
        for(GroupEntry<TbItem> ge:page){
            list.add( ge.getGroupValue());//得到分组的结果集
        }
        return list;
    }


    /**
     * 根据分类查询brandList和specList
     * categoryName--->typid
     * typeid----> brandList
     * typeid----->sprcList
     * @param categoryName
     * @return
     */
    private Map searchBrandAndSpecList(String categoryName){
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        if(typeId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList",brandList);
            map.put("specList",specList);
        }
        return map;
    }




    //显示记录数据
    private void showList(List<TbItem> list){
        for(TbItem item:list){
            System.out.println(item.getTitle() +item.getPrice());
        }
    }
}
