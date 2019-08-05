package com.youlexuan.cart.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.youlexuan.cart.service.CartService;
import com.youlexuan.entity.Cart;
import com.youlexuan.mapper.TbItemMapper;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemid, Integer num) {
        //1。根据商品SKU ID查询SKU信息
        TbItem item = itemMapper.selectByPrimaryKey(itemid);
        if (item == null){
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }
        //2。获取商家ID
        String sellerId= item.getSellerId();
        String sellerName = item.getSeller();

        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList,sellerId);

        //4.如果购物车列表中不存在该商家的购物车
        if (cart == null){
            //4.1 新建购物车
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellName(sellerName);
            TbOrderItem orderItem = createOrderItem(item,num);
            ArrayList<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2 将购物车对象添加到购物车列表
            cartList.add(cart);
        }else {
            //5.有购物车 判断里面是否有该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemid);
            if (orderItem == null){
                //5.1如果没有就新增
                orderItem=createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else {
                //5.2如果有，就添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
            }
            if (orderItem.getNum()<=0){
                cart.getOrderItemList().remove(orderItem);
            }
            if (cart.getOrderItemList().size()==0){
                cartList.remove(cart);
            }
        }


        return cartList;
    }

    /**
     * 根据商家ID查询购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for(Cart cart:cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据商品明细ID查询
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList ,Long itemId ){
        for(TbOrderItem orderItem :orderItemList){
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建订单明细
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num<=0){
            throw new RuntimeException("数量非法");
        }

        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

}
