package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 实验室展示信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
public interface ITeamService extends IService<Team>
{

    /**
     * 保存组队信息
     * @param team 组队信息
     * @return 结果
     */
    boolean saveTeam(Team team);

    /**
     * 更新组队信息
     * @param team 组队信息
     * @return 结果
     */
    boolean updateTeamById(Team team);

    /**
     * 分页查询
     * @param currentPage 当前页
     * @param pageSize 每页记录数
     * @param name 名称
     * @param code 代码
     * @param creator  创建人
     * @return 结果
     */
    IPage<Team> selectPage(long currentPage, long pageSize, String name, String code, String creator);

    /**
     * 根据id查单条
     * @param id id
     * @return 结果
     */
    Team getTeamById(String id);

    /**
     * 查询所有的组队
     * @param userId 用户id
     * @param name 名称
     * @param code 代码
     * @return 结果
     */
    List<Team> getAll(String userId, String name, String code);

    /**
     * 查询赛事的队伍信息
     * @param playId 赛事id
     * @return 结果
     */
    IPage<Team> selectPlayPage(long currentPage, long pageSize, String name, String code, String creator, String playId);

    /**
     * 认证成绩
     * @param file 文件
     * @param playId 赛事id
     * @param teamId 组队id
     * @param score 成绩
     * @return 结果
     */
    boolean certificationScore(MultipartFile file, String playId, String teamId, String score) throws Exception;

    /**
     *  查询赛事的队伍信息
     * @param playId
     * @return 结果
     */
    List<Team> getByPlayId(String playId);
}
