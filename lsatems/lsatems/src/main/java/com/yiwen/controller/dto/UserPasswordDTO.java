package com.yiwen.controller.dto;

import lombok.Data;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-21
 * @see UserPasswordDTO
 **/
@Data
public class UserPasswordDTO
{
    private String id;
    private String password;
    private String newPassword;
}
