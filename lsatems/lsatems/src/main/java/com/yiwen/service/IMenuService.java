package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.domain.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-15
 */
public interface IMenuService extends IService<Menu> {


    /**
     * 查询菜单
     * @param name 名称
     * @return 结果
     */
    List<Menu> findAll(String name);

    /**
     * 根据id删除菜单
     * @param id
     * @return 结果
     */
    boolean removeMenusById(String id);

    /**
     * 批量删除菜单
     * @param ids
     * @return 结果
     */
    boolean removeMenusByIds(List<String> ids);
}
