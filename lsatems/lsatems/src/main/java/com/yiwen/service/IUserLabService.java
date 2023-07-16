package com.yiwen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.controller.dto.UserShowDTO;
import com.yiwen.domain.UserLab;

import java.util.List;

/**
 * <p>
 * 用户与实验室关系表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-22
 */
public interface IUserLabService extends IService<UserLab> {

    /**
     * 保存申请关系
     * @param userId 用户id
     * @param labId 实验室id
     * @return 结果
     */
    boolean saveById(String userId, String labId, String content);

    /**
     * 获取实验室的管理id
     * @param labId 实验室id
     * @return 结果
     */
    List<String> getLabAdminIds(String labId);

    /**
     * 更新申请实验室状态
     * @param userLab 用户和实验室关系
     * @return 结果
     */
    boolean updateStatus(UserLab userLab);

    /**
     * 根据用户id查询关系
     * @param userId 用户id
     * @return 结果
     */
    List<UserLab> getByUserId(String userId);

    /**
     * 退出实验室
     * @param userId 用户id
     * @param labId 实验室id
     * @return 结果
     */
    boolean quitLab(String userId, String labId);

    /**
     * 根据labId查询用户实验室关系
     * @param labId labId
     * @return 结果
     */
    List<UserLab> getByLabId(String labId);

    /**
     * 查询实验成员
     * @param labId 实验室id
     * @return 结果
     */
    List<UserShowDTO> getMembers(String labId);

    /**
     * 任命管理
     * @param labId 实验室id
     * @param userId  用户id
     * @return 结果
     */
    List<UserShowDTO> commissionAdmin(String labId, String userId);

    /**
     * 取消任命管理
     * @param labId 实验室id
     * @param userId 用户id
     * @return 结果
     */
    List<UserShowDTO> cancelCommissionAdmin(String labId, String userId);

    /**
     * 强制提出
     * @param labId 实验室id
     * @param userId 用户Id
     * @return 结果
     */
    List<UserShowDTO> forcePropose(String labId, String userId);
}
