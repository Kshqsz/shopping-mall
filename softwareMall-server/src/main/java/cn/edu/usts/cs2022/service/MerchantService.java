package cn.edu.usts.cs2022.service;

import cn.edu.usts.cs2022.pojo.dto.UserUpdateDTO;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.vo.MerchantVO;

import java.util.List;

public interface MerchantService {
    Merchant login(String username, String password);

    Merchant getMerchantByUsername(String username);

    void register(String username, String password);

    /**
     * 查询所有商家
     * @return
     */
    List<Merchant> getAllMerchant();

    /**
     * 修改商家状态
     * @param merchant
     */
    void changeStatus(Merchant merchant);

    MerchantVO getByProductId(Integer id);

    Merchant getById(Integer id);

    void updatePassword(String newPassword);

    void update(UserUpdateDTO userUpdateDTO);
}
