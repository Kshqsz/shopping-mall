package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.UserMapper;
import cn.edu.usts.cs2022.pojo.dto.AddressDto;
import cn.edu.usts.cs2022.pojo.dto.CountOrderDTO;
import cn.edu.usts.cs2022.pojo.dto.UserUpdateDTO;
import cn.edu.usts.cs2022.pojo.po.Address;
import cn.edu.usts.cs2022.pojo.po.Favourite;
import cn.edu.usts.cs2022.pojo.po.User;
import cn.edu.usts.cs2022.pojo.vo.client.AddressVo;
import cn.edu.usts.cs2022.service.UserService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUserName(username);
    }

    @Override
    public void register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.register(user);
    }

    /**
     * 获取全部用户
     *
     * @return
     */
    @Override
    public List<User> getUserList() {
        return userMapper.selectAllUsers();
    }

    /**
     * 修改用户状态
     * @param user
     */
    @Override
    public void changeUserStatus(User user) {
        userMapper.changeStatus(user);
    }

    @Override
    public List<Favourite> getMyFavourite(Integer userId) {
        return userMapper.getMyFavourite(userId);
    }

    @Override
    public Integer countFavourite(Integer userId) {
        return userMapper.countFavourite(userId);
    }

    @Override
    public void updatePassword(String newPassword) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        userMapper.updatePassword(userId, newPassword);
    }

    @Override
    public void update(UserUpdateDTO userUpdateDTO) {
        userMapper.update(userUpdateDTO);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public Integer countOrder(CountOrderDTO countOrderDTO) {
        return userMapper.countOrder(countOrderDTO);
    }

    //新增收获地址
    @Transactional
    @Override
    public void addAddress(AddressDto addressDto) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        Address address = new Address();
        address.setUserId(userId);
        System.out.println(addressDto.toString());
        address.setName(addressDto.getName());
        address.setPhone(addressDto.getPhone());
        address.setDetail(addressDto.getDetail());
        address.setIsDefault(addressDto.getIsDefault());
        address.setCity(addressDto.getRegion().get(0));
        address.setProvince(addressDto.getRegion().get(1));
        address.setDistrict(addressDto.getRegion().get(2));

        userMapper.addAddress(address);
        if (address.getIsDefault()){
            userMapper.unDefault(address);
            userMapper.setDefault(address);
        }
    }
    //新增地址
    @Override
    public List<AddressVo> addressList() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        List<AddressVo> addressVos = userMapper.addressList(userId);
        return addressVos;
    }
    //设为默认地址
    @Override
    public void toDefault(Integer id) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        Address address = new Address();
        address.setUserId(userId);
        address.setId(id);
        userMapper.unDefault(address);
        userMapper.setDefault(address);
    }

    //修改地址
    @Override
    public void updateAddress(AddressDto addressDto) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        Address address = new Address();
        address.setId(addressDto.getId());
        address.setUserId(userId);
        address.setName(addressDto.getName());
        address.setPhone(addressDto.getPhone());
        address.setDetail(addressDto.getDetail());
        address.setIsDefault(addressDto.getIsDefault());
        address.setCity(addressDto.getRegion().get(0));
        address.setProvince(addressDto.getRegion().get(1));
        address.setDistrict(addressDto.getRegion().get(2));
        userMapper.updateAddress(address);
        if (address.getIsDefault()){
            userMapper.unDefault(address);
            userMapper.setDefault(address);
        }
    }
}
