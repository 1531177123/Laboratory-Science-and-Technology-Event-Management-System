package com.yiwen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiwen.domain.TeamRemark;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 科技赛事评论表 Mapper 接口
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
public interface TeamRemarkDao extends BaseMapper<TeamRemark> {

    @Select("select c.*,u.name,u.avatar_path from tbl_team_remark c " +
            "left join tbl_user_detail u on c.user_id = u.user_id where c.team_id = #{teamId} order by c.create_time desc")
    List<TeamRemark> findRemarkDetails(@Param("teamId") String teamId);
}
