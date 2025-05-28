package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.AdminMapper;
import cn.edu.usts.cs2022.pojo.po.Admin;
import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.po.User;
import cn.edu.usts.cs2022.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    public final AdminMapper adminMapper;
    @Override
    public Admin login(String username, String password) {
        return adminMapper.login(username, password);
    }





    /**
     * 获取所有商家
     * @return
     */
    @Override
    public List<Merchant> getAllMerchant() {
        return adminMapper.selectMerchant();
    }


}
