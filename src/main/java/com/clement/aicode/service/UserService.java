package com.clement.aicode.service;

import com.clement.aicode.model.dto.UserLoginRequest;
import com.clement.aicode.model.dto.UserRegisterRequest;
import com.clement.aicode.model.vo.UserLoginVO;
import com.clement.aicode.model.vo.UserVO;

public interface UserService {

    UserVO register(UserRegisterRequest request);

    UserLoginVO login(UserLoginRequest request);

    UserVO getCurrentUser();
}
