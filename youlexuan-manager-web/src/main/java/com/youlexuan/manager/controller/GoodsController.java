package com.youlexuan.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.youlexuan.group.Goods;
import com.youlexuan.page.service.ItemPageService;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.search.service.SearchService;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.youlexuan.pojo.TbGoods;
import com.youlexuan.sellergoods.service.GoodsService;

import com.youlexuan.entity.PageResult;
import com.youlexuan.entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference(timeout = 5000000)
	private SearchService searchService;

	@Reference(timeout=40000)
	private ItemPageService itemPageService;
	/**
	 * 生成静态页（测试）
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		itemPageService.genItemPage(goodsId);
	}
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){

		try {
			goodsService.updateStatus(ids,status);
			/**
			 * 从数据库中查询符合条件的tbItemss
			 */
			/*if("1".equals(status)){

				//审核通过以后，通过solrs索引库
				List<TbItem> tbItemList = goodsService.findItemListByGoodsIdsStauts(ids, "1");
				if(tbItemList!=null&& tbItemList.size()>0) {
					//不直接 调用searchService 跟新索引库，而是发送一条mq。让searchService监听mq,自行更新
					//searchService.importList(tbItemList);
					String jsonString = JSON.toJSONString(tbItemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
				}
				//审核通过需要将页面进行静态化处理
				for(Long goodsId:ids){
					itemPageService.genItemPage(goodsId);
				}
			}*/
			if ("1".equals(status)){
				List<TbItem> tbItemList = goodsService.findItemListByGoodsIdsStauts(ids, "1");
				if (tbItemList!=null&&tbItemList.size()>0){
					System.out.println("进入新增索引方法");
					searchService.importList(tbItemList);
				}
				System.out.println(tbItemList.size());
				for(Long goodsId:ids){
					itemPageService.genItemPage(goodsId);
				}
			}
			return  new Result(true,"修改状态成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,e.toString());
		}
	}
	
}
