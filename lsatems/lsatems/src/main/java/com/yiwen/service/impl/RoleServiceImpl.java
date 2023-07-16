package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiwen.dao.RoleMenuDao;
import com.yiwen.domain.Role;
import com.yiwen.dao.RoleDao;
import com.yiwen.domain.RoleMenu;
import com.yiwen.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-14
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements IRoleService {


    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleMenuDao roleMenuDao;

    @Override
    public IPage<Role> selectPage(long currentPage, long pageSize, String name)
    {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Role::getName, name);
        IPage<Role> rolePage = roleDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        return rolePage;
    }

    @Transactional
    @Override
    public void setRoleMenu(String roleCode, List<String> menuIds)
    {
        roleMenuDao.deleteByRoleCode(roleCode);
        for (String menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleCode(roleCode);
            roleMenuDao.insert(roleMenu);
        }
    }

    @Override
    public List<String> getRoleMenu(String roleCode)
    {
        return roleMenuDao.selectByRoleCode(roleCode);
    }

    @Override
    public boolean removeRoleById(String id)
    {
        boolean roleFlag = removeById(id);
        Role role = roleDao.selectById(id);
        roleMenuDao.deleteByRoleCode(role.getCode());
        return roleFlag;
    }

    @Override
    public boolean removeRolesByIds(List<String> ids)
    {
        boolean rolesFlag = removeByIds(ids);
        List<Role> roles = roleDao.selectBatchIds(ids);
        for (Role role : roles) {
            roleMenuDao.deleteByRoleCode(role.getCode());
        }
        return rolesFlag;
    }

    @Override
    public boolean saveRole(Role role) {

        return checkRoleCode(role) && save(role);
    }

    @Override
    public boolean updateRoleById(Role role) {
        return checkRoleCode(role) && updateById(role);
    }

    private boolean checkRoleCode(Role role)
    {
        String code = role.getCode();
        if (StrUtil.isBlank(code))
        {
            return false;
        }
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getCode, code);
        queryWrapper.ne(StrUtil.isNotBlank(role.getId()), Role::getId, role.getId());
        Role role1 = roleDao.selectOne(queryWrapper);
        if (role1 != null)
        {
            return  false;
        }
        return  true;
    }

}
