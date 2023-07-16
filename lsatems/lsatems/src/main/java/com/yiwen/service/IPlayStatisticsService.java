package com.yiwen.service;

import com.yiwen.controller.dto.EchartsDTO;
import com.yiwen.controller.dto.PlayRankListDTO;
import com.yiwen.controller.dto.PlayStatisticsDTO;
import com.yiwen.domain.EchartHandle;

import java.util.List;
import java.util.Map;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-27
 * @see IPlayStatisticsService
 **/
public interface IPlayStatisticsService
{
    /**
     * 查询实验室排行版统计
     * @param topN 前N名
     * @return 结果
     */
    PlayRankListDTO getLabsRankList(Integer topN);

    /**
     * 查询个人排行版统计
     * @param topN 前N名
     * @return 结果
     */
    PlayRankListDTO getPersonRankList(Integer topN);

    /**
     * 查询基本赛事统计数据
     * @return 结果
     */
    PlayStatisticsDTO getPlayStatistics();

    /**
     * 查询实验室获奖数据
     * @return 结果
     */
    EchartsDTO getLabAwardData();

    /**
     * 查询实验室获奖数据（饼图）
     * @return 结果
     */
    List<EchartHandle> getLabAwardPieData();
}
