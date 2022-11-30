package com.zht.springboot.controller.dto;

import com.zht.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String nickname;
    private String token;
    private String role;
    private List<Menu> menus;
}
