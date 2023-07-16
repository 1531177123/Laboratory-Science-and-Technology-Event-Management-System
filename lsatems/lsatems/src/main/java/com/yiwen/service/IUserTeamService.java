package com.yiwen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.UserLab;
import com.yiwen.domain.UserTeam;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 实验室展示信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
public interface IUserTeamService extends IService<UserTeam> {

    /**
     * 申请组队
     * @param userId 用户id
     * @param teamId 组队id
     * @param content 申请内容
     * @return 结果
     */
    boolean saveById(String userId, String teamId, String content);

    /**
     * 更新组队申请状态
     * @param userTeam 用户和组队关系
     * @return 结果
     */
    boolean updateStatus(UserTeam userTeam);

    /**
     * 退出组队
     * @param userId 用户id
     * @param teamId 组队id
     * @return 结果
     */
    boolean quitTeam(String userId, String teamId);

    /**
     * 查询用户的组队关系
     * @param userId 用户id
     * @return 结果
     */
    List<UserTeam> getByUserId(String userId);

    /**
     * 根据组队id查关系
     * @param teamId 组队id
     * @return 结果
     */
    List<UserTeam> getByTeamId(String teamId);
}
