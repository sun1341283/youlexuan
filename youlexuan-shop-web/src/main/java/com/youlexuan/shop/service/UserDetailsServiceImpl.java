package com.youlexuan.shop.service;

import com.youlexuan.pojo.TbSeller;
import com.youlexuan.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * 认证类
     * @author Administrator
     *
     */
        private SellerService sellerService;
        public void setSellerService(SellerService sellerService) {
            this.sellerService = sellerService;
        }
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            System.out.println("经过了UserDetailsServiceImpl");
            //构建角色列表
            List<GrantedAuthority> grantAuths=new ArrayList<>();
            grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
            //得到商家对象
            TbSeller seller = sellerService.findOne(username);
            if(seller!=null){
                if("1".equals(seller.getStatus())){
                    return new User(username,seller.getPassword(),grantAuths);
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }

}
