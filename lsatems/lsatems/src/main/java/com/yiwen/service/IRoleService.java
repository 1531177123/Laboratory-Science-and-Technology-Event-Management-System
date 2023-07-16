package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.dao.RoleDao;
import com.yiwen.domain.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-14
 */
public interface IRoleService extends IService<Role> {

    /**
     * 分页查询
     * @param currentPage 当前页码
     * @param pageSize 每页数据量
     * @param name 名字
     * @return 结果
     */
    IPage<Role> selectPage(long currentPage, long pageSize, String name);

    /**
     * 设置角色菜单关系
     * @param roleCode 角色代码
     * @param menuIds 菜单id
     * @return 结果
     */
    void setRoleMenu(String roleCode, List<String> menuIds);

    /**
     * 活得角色的菜单
     * @param roleCode 角色代码
     * @return 结果
     */
    List<String> getRoleMenu(String roleCode);

    /**
     * 根据id删除角色
     * @param id
     * @return 结果
     */
    boolean removeRoleById(String id);

    /**
     * 批量删除角色
     * @param ids
     * @return 结果
     */
    boolean removeRolesByIds(List<String> ids);

    /**
     * 新增角色
     * @param role
     * @return 结果
     */
    boolean saveRole(Role role);

    /**
     * 更新角色
     * @param role
     * @return 结果
     */
    boolean updateRoleById(Role role);
}
