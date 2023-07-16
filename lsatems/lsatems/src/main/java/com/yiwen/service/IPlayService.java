package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.domain.Play;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 科技赛事详细信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-24
 */
public interface IPlayService extends IService<Play> {

    /**
     * 分页查询科技赛事
     * @return 结果
     */
    IPage<Play> selectPage(long currentPage, long pageSize, String name, String labId, String priority);

    /**
     * 新增科技赛事
     * @param play 科技赛事
     * @return 结果
     */
    boolean savePlay(Play play);

    /**
     * 更新科技赛事
     * @param play 科技赛事
     * @return 结果
     */
    boolean updatePlayById(Play play);

    /**
     *  查询实验室的科技赛事
     * @param labId  实验室id
     * @return 结果
     */
    List<Play> getByLabId(String labId);

    /**
     *  查询实验室的科技赛事
     * @param labId  实验室id
     * @return 结果
     */
    List<Play> getByLabIdAndMix(String labId, String name, String priority);

}
