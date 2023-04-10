package com.hsy.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsy.springboot.common.Constants;
import com.hsy.springboot.controller.dto.UserDTO;
import com.hsy.springboot.controller.dto.UserPasswordDTO;
import com.hsy.springboot.entity.Menu;
import com.hsy.springboot.entity.User;
import com.hsy.springboot.exception.ServiceException;
import com.hsy.springboot.mapper.RoleMapper;
import com.hsy.springboot.mapper.RoleMenuMapper;
import com.hsy.springboot.mapper.UserMapper;
import com.hsy.springboot.service.IMenuService;
import com.hsy.springboot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hsy.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hsy
 * @since 2023-04-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Log LOG =Log.get();

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private IMenuService menuService;
    @Override
    public UserDTO login(UserDTO userDTO) {
        User userInfo = getUserInfo(userDTO);
        if(userInfo != null){
            BeanUtil.copyProperties(userInfo,userDTO,true); //将userDTO中的数据拷贝到user中,忽略大小写
            //设置token
            String token = TokenUtils.genToken(userInfo.getId().toString(), userInfo.getPassword());
            userDTO.setToken(token);
            //当前用户的角色
            String role=userInfo.getRole(); // ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);
            return userDTO;
        }else{
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {
        User userInfo = getUserInfo(userDTO);
        if(userInfo == null){
            User newUser = new User();
            BeanUtil.copyProperties(userDTO,newUser,true); //将前端数据复制给实体类
            save(newUser);
        }else {
            throw new ServiceException(Constants.CODE_600,"用户已存在");
        }
        return userInfo;
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if(update<1){
            throw new ServiceException(Constants.CODE_600,"密码错误");
        }
    }

    @Override
    public Page<User> findPage(Page<User> page, String username, String email, String address) {
        return userMapper.findPage(page,username,email,address);
    }


    public User getUserInfo(UserDTO userDTO){  //用于数据校验
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",userDTO.getUsername());
        queryWrapper.eq("password",userDTO.getPassword());
        User userOne;
        // 处理异常情况
        try {
            userOne = getOne(queryWrapper);  //数据唯一
        }catch (Exception e){
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        return userOne;
    }

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag){
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        //角色对应的菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
        // 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.findMenus("");
        //new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        //筛选当前角色的菜单
        for (Menu menu: menus) {
            if(menuIds.contains(menu.getId())){
                roleMenus.add(menu);
            }
            List<Menu> children=menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child->!menuIds.contains(child.getId()));
        }
        return roleMenus;
    }
}
