package com.yiwen.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-28
 * @see RankListStatistics
 **/
@Data
public class RankListStatistics
{
    private String name;
    private Map<String, String> yAxis;
}
