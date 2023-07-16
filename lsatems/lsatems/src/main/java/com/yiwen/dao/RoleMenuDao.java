package com.yiwen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiwen.domain.Menu;
import com.yiwen.domain.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yiwen
 * @since 2023-03-14
 */
public interface RoleMenuDao extends BaseMapper<RoleMenu> {

    @Delete("delete from tbl_role_menu where role_code = #{roleCode}")
    int deleteByRoleCode(String roleCode);

    @Select("select menu_id from tbl_role_menu where role_code = #{roleCode}")
    List<String> selectByRoleCode(String roleCode);

    @Delete("delete from tbl_role_menu where menu_id = #{id}")
    int deleteByMenuId(String id);
}
