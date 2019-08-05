package com.youlexuan.cart.service;

import com.youlexuan.entity.Cart;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList 购物车列表
     * @param itemid
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemid,Integer num);
}
