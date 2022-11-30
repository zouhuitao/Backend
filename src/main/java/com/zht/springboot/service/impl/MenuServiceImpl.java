package com.zht.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zht.springboot.entity.Menu;
import com.zht.springboot.mapper.MenuMapper;
import com.zht.springboot.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tao
 * @since 2022-11-13
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> queryWrapper= new QueryWrapper<>();
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name", name);
        }

        queryWrapper.orderByDesc("id");
        List<Menu> list = list(queryWrapper);
        List<Menu> parentNodes =list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        for (Menu menu : parentNodes) {
            menu.setChildren(list.stream().filter(m -> menu.getId() .equals(m.getPid())).collect(Collectors.toList()));
        }
        return parentNodes;
    }
}
