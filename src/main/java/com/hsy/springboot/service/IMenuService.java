package com.hsy.springboot.service;

import com.hsy.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hsy
 * @since 2023-04-05
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
