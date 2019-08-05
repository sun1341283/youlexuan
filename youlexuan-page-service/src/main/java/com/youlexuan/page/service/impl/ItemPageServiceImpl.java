package com.youlexuan.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.youlexuan.mapper.TbGoodsDescMapper;
import com.youlexuan.mapper.TbGoodsMapper;
import com.youlexuan.mapper.TbItemCatMapper;
import com.youlexuan.mapper.TbItemMapper;
import com.youlexuan.page.service.ItemPageService;
import com.youlexuan.pojo.TbGoods;
import com.youlexuan.pojo.TbGoodsDesc;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 50000)
public class ItemPageServiceImpl implements ItemPageService {


    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${pagedir}")
    private String pageDir;

    /**
     * 1、查询数据库，得到商品的基本信息、扩展信息、sku信息
     * 2、加载ft模板文件使用freemarker生成html 静态页面
     *    node: 生成的静态文件理论上是可以放到任何位置的，然后使用Nginx动静分离访问即可
     *          但是我们的项目中为了方便测试，放到了youlexuan-page-web中，借助tomcat访问
     *          生成的html文件命名为 goodsId.html
     * @param goodsId
     * @return
     */
    @Override
    public boolean genItemPage(Long goodsId)  {

        //查询商品信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        TbItemExample exam = new TbItemExample();
        exam.createCriteria().andGoodsIdEqualTo(goodsId);
        List<TbItem> itemList = itemMapper.selectByExample(exam);
        Writer writer = null;
        try {
            //生成html页面
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            Map map = new HashMap();
            map.put("goods",tbGoods);
            map.put("goodsDesc",tbGoodsDesc);
            map.put("itemList",itemList);

            //面包屑导航
            String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            map.put("itemCat1",itemCat1);
            map.put("itemCat2",itemCat2);
            map.put("itemCat3",itemCat3);


            writer = new FileWriter(new File(pageDir+goodsId+".html"));
            template.process(map,writer);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(goodsId+"的广告详情页生成完毕");
        }

    }
}
