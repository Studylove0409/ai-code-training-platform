package com.clement.aicode.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
