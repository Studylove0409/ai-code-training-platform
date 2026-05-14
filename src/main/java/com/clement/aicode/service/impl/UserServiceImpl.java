package com.clement.aicode.service.impl;

import com.clement.aicode.common.BusinessException;
import com.clement.aicode.common.ErrorCode;
import com.clement.aicode.common.UserContext;
import com.clement.aicode.mapper.UserMapper;
import com.clement.aicode.model.dto.UserLoginRequest;
import com.clement.aicode.model.dto.UserRegisterRequest;
import com.clement.aicode.model.entity.User;
import com.clement.aicode.model.vo.UserLoginVO;
import com.clement.aicode.model.vo.UserVO;
import com.clement.aicode.service.UserService;
import com.clement.aicode.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final int USER_STATUS_NORMAL = 1;
    private static final int USER_STATUS_DISABLED = 0;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(UserRegisterRequest request) {
        checkRegisterRequest(request);

        User existedUser = userMapper.findByUsername(request.getUsername());
        if (existedUser != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setStatus(USER_STATUS_NORMAL);

        userMapper.insert(user);

        User savedUser = userMapper.findByUsername(request.getUsername());
        return toUserVO(savedUser);
    }

    @Override
    public UserLoginVO login(UserLoginRequest request) {
        checkLoginRequest(request);

        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名或密码错误");
        }
        if (Integer.valueOf(USER_STATUS_DISABLED).equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
        }

        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        loginVO.setUser(toUserVO(user));
        return loginVO;
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return toUserVO(user);
    }

    private void checkRegisterRequest(UserRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求参数不能为空");
        }
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码不能为空");
        }
        if (!StringUtils.hasText(request.getNickname())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "昵称不能为空");
        }
        if (request.getUsername().length() > 64) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名长度不能超过 64 个字符");
        }
        if (request.getPassword().length() < 6 || request.getPassword().length() > 32) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码长度必须在 6 到 32 个字符之间");
        }
        if (request.getNickname().length() > 64) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "昵称长度不能超过 64 个字符");
        }
        if (StringUtils.hasText(request.getEmail()) && request.getEmail().length() > 128) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "邮箱长度不能超过 128 个字符");
        }
    }

    private void checkLoginRequest(UserLoginRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求参数不能为空");
        }
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码不能为空");
        }
    }

    private UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setEmail(user.getEmail());
        return userVO;
    }
}
