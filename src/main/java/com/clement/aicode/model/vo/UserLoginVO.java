package com.clement.aicode.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginVO {

    private String token;

    private UserVO user;
}
