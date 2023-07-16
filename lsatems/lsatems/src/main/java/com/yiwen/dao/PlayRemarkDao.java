package com.yiwen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiwen.domain.PlayRemark;
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
public interface PlayRemarkDao extends BaseMapper<PlayRemark> {

    @Select("select c.*,u.name,u.avatar_path from tbl_play_remark c " +
            "left join tbl_user_detail u on c.user_id = u.user_id where c.play_id = #{playId} order by c.create_time desc")
    List<PlayRemark> findRemarkDetails(@Param("playId") String playId);
}
