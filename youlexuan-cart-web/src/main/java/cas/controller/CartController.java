package cas.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.youlexuan.cart.service.CartService;
import com.youlexuan.entity.Result;
import com.youlexuan.entity.Cart;
import com.youlexuan.util.CookieUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference()
    private CartService cartService;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String cartListString = CookieUtil.getCookieValue(request,"cartList","UTF-8");
        if (cartListString ==null || cartListString.equals("")){
            cartListString="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString,Cart.class);
        return cartList_cookie;
    }

    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            List<Cart> cartList = findCartList();
            System.out.println(cartList+"/"+itemId+"/"+num);
            cartList = cartService.addGoodsToCartList(cartList,itemId,num);
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
            return new Result(true,"添加成功！");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }

    }




}



