package com.zht.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zht.springboot.controller.dto.UserDTO;
import com.zht.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tao
 * @since 2022-11-02
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);


    Object findPage(Page<Object> objectPage, String username, String email, String address);
}
