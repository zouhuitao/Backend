package com.zht.springboot.service;

import com.zht.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tao
 * @since 2022-11-13
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
