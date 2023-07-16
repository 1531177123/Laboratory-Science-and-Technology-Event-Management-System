package com.yiwen.controller.dto;

import com.yiwen.domain.RankListStatistics;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-28
 * @see PlayRankListDTO
 **/
@Data
public class PlayRankListDTO
{
    private Set<String> crossAxis;

    private List<RankListStatistics> verticalAxis;
}
