package com.yiwen.controller.dto;

import lombok.Data;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-22
 * @see LabShowDTO
 **/
@Data
public class LabShowDTO
{
    private String id;
    private String name;
    private String avatarPath;
    private String type;
    private String description;
    private String creatorId;
}
