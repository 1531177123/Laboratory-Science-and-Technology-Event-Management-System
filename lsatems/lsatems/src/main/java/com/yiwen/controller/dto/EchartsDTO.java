package com.yiwen.controller.dto;

import lombok.Data;

import java.util.List;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-30
 * @see EchartsDTO
 **/
@Data
public class EchartsDTO
{
    private List<String> xAxis;

    private List<String> yAxis;
}
