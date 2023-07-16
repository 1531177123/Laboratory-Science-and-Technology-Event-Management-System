package com.yiwen.controller.dto;

import lombok.Data;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-11
 * @see UserShowDTO
 **/
@Data
public class UserShowDTO
{
    private String id;
    private String name;
    private String avatarPath;
    private String roleType;
}
