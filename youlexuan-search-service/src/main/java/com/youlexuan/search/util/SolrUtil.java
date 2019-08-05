package com.youlexuan.search.util;

import com.alibaba.fastjson.JSON;
import com.youlexuan.mapper.TbItemMapper;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 查mysql的数据库，ibitem表中所有的数据，入到solr索引库中
 * 只是项目上线时入库一次即可
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    //导入索引库
    /*public void importData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过的
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        System.out.println("===商品列表开始导入");
        for(TbItem tbItem:tbItems){
            Map jsonObject = JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSpecMap(jsonObject);
            System.out.println(tbItem.getTitle());
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("===索引库导入结束===");

    }
    public static void main(String[] args){
        //去spring配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil  solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importData();
    }*/


}
