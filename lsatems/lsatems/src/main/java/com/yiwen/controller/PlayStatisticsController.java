package com.yiwen.controller;

import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.controller.dto.EchartsDTO;
import com.yiwen.controller.dto.PlayRankListDTO;
import com.yiwen.controller.dto.PlayStatisticsDTO;
import com.yiwen.domain.EchartHandle;
import com.yiwen.service.IPlayStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-27
 * @see PlayStatisticsController
 **/
@RestController
@RequestMapping("/playStatistics")
public class PlayStatisticsController
{
    @Autowired
    private IPlayStatisticsService playStatisticsService;

    @GetMapping("/getLabsRankList")
    public Result getLabsRankList(@RequestParam String topN)
    {
        PlayRankListDTO labsRankList = playStatisticsService.getLabsRankList(Integer.valueOf(topN.replace("Top", "")));
        if (labsRankList != null )
        {
            return new Result(Code.GET_OK, labsRankList, "查询实验室排行榜数据成功");
        }
        else
        {
            return new Result(Code.GET_OK, null, "查询实验室排行榜数据失败");
        }

    }

    @GetMapping("/getPersonRankList")
    public Result getPersonRankList(@RequestParam String topN)
    {
        PlayRankListDTO  personRankList = playStatisticsService.getPersonRankList(Integer.valueOf(topN.replace("Top", "")));
        if (personRankList != null )
        {
            return new Result(Code.GET_OK, personRankList, "查询个人排行榜数据成功");
        }
        else
        {
            return new Result(Code.GET_OK, null, "查询个人排行榜数据失败");
        }

    }

    @GetMapping("/getPlayStatistics")
    public Result getPlayStatistics()
    {
        PlayStatisticsDTO  statisticsDTO = playStatisticsService.getPlayStatistics();
        if (statisticsDTO != null )
        {
            return new Result(Code.GET_OK, statisticsDTO, "查询基本赛事统计数据成功");
        }
        else
        {
            return new Result(Code.GET_OK, null, "查询基本赛事统计数据失败");
        }
    }

    @GetMapping("/getLabAwardData")
    public Result getLabAwardData()
    {
        EchartsDTO labAwardData = playStatisticsService.getLabAwardData();
        if (labAwardData != null )
        {
            return new Result(Code.GET_OK, labAwardData, "查询成功");
        }
        else
        {
            return new Result(Code.GET_OK, null, "查询失败");
        }
    }

    @GetMapping("/getLabAwardPieData")
    public Result getLabAwardPieData()
    {
        List<EchartHandle> labAwardData = playStatisticsService.getLabAwardPieData();
        if (labAwardData != null )
        {
            return new Result(Code.GET_OK, labAwardData, "查询成功");
        }
        else
        {
            return new Result(Code.GET_OK, null, "查询失败");
        }
    }


}
