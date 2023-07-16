package com.yiwen.controller.dto;

import com.yiwen.domain.Menu;
import lombok.Data;

import java.util.List;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-11
 * @see UserLoginDTO
 **/
@Data
public class UserLoginDTO
{
    private String id;
    private String username;
    private String password;
    private String roleType;
    private String detailFlag;
    private Boolean remember;
    private String token;
    private List<Menu> menus;
}
