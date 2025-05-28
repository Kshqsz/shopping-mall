package cn.edu.usts.cs2022.service;

import cn.edu.usts.cs2022.pojo.po.Admin;
import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.po.User;

import java.util.List;

public interface AdminService {
    Admin login(String username, String password);


    /**
     * 获取所有商家
     * @return
     */
    List<Merchant> getAllMerchant();



}
