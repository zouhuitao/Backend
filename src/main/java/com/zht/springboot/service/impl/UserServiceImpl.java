package com.zht.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zht.springboot.common.Constants;
import com.zht.springboot.controller.dto.UserDTO;
import com.zht.springboot.entity.Menu;
import com.zht.springboot.entity.User;
import com.zht.springboot.exception.ServiceException;
import com.zht.springboot.mapper.RoleMapper;
import com.zht.springboot.mapper.RoleMenuMapper;
import com.zht.springboot.mapper.UserMapper;
import com.zht.springboot.service.IMenuService;
import com.zht.springboot.service.IUserService;
import com.zht.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tao
 * @since 2022-11-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Log LOG = Log.get();

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one!=null){
            BeanUtil.copyProperties(one,userDTO,true);
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);

            String role = one.getRole();

            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);
            return  userDTO;
        }else {
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if(one == null){
            one = new User();
            BeanUtil.copyProperties(userDTO,one,true);
            save(one);
        }else {
            throw new ServiceException(Constants.CODE_600,"用户已存在");
        }
        return null;
    }

    @Override
    public Object findPage(Page<Object> objectPage, String username, String email, String address) {
        return null;
    }

    private User getUserInfo(UserDTO userDTO){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try{
            one = getOne(queryWrapper);

        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        return  one;
    }

    private List<Menu> getRoleMenus(String roleFlag) {

        Integer roleId = roleMapper.selectByFlag(roleFlag);
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        List<Menu> menus = menuService.findMenus("");
        List<Menu> roleMenus = new ArrayList<>();

        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
        }
    }
