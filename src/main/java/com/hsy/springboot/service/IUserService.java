package com.hsy.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsy.springboot.controller.dto.UserDTO;
import com.hsy.springboot.controller.dto.UserPasswordDTO;
import com.hsy.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hsy
 * @since 2023-04-03
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> page, String username, String email, String address);
}
