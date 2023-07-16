package com.yiwen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.TeamRemark;

import java.util.List;

/**
 * <p>
 * 科技赛事评论表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
public interface ITeamRemarkService extends IService<TeamRemark> {

    /**
     * 保存赛事组队评论
     * @param teamRemark 评论
     * @return 结果
     */
    boolean saveTeamRemark(TeamRemark teamRemark);

    /**
     * 更新赛事组队评论
     * @param teamRemark 评论
     * @return 结果
     */
    boolean updateTeamRemarkById(TeamRemark teamRemark);

    /**
     * 查询赛事组队评论
     * @param teamId 赛事id
     * @return 结果
     */
    List<TeamRemark> getTreeByTeamId(String teamId);
}
