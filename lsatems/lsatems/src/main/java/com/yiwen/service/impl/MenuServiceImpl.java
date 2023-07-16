package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiwen.dao.RoleMenuDao;
import com.yiwen.domain.Menu;
import com.yiwen.dao.MenuDao;
import com.yiwen.domain.Role;
import com.yiwen.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-15
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements IMenuService
{

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private RoleMenuDao roleMenuDao;

    @Override
    public List<Menu> findAll(String name)
    {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Menu::getName, name);
        queryWrapper.orderByAsc(Menu::getSeq);
        List<Menu> list = list(queryWrapper);
        //找出一级
        List<Menu> parentMenus = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        if (!parentMenus.isEmpty())
        {
            //找出二级
            for (Menu parentMenu : parentMenus)
            {
                List<Menu> childMenus = list.stream().filter(menu -> parentMenu.getId().equals(menu.getPid())).collect(Collectors.toList());
                parentMenu.setChildren(childMenus);
            }
            return parentMenus;
        }

        return list;
    }

    @Override
    public boolean removeMenusById(String id)
    {
        boolean menusFlag = removeById(id);
        roleMenuDao.deleteByMenuId(id);
        return menusFlag;
    }

    @Override
    public boolean removeMenusByIds(List<String> ids)
    {
        boolean menusFlags = removeByIds(ids);
        for (String id : ids) {
            roleMenuDao.deleteByMenuId(id);
        }
        return menusFlags;
    }
}
