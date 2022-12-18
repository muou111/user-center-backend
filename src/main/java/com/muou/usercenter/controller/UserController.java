package com.muou.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muou.usercenter.common.BaseResponse;
import com.muou.usercenter.common.ErrorCodeEnum;
import com.muou.usercenter.common.ResultUtils;
import com.muou.usercenter.exception.BusinessException;
import com.muou.usercenter.model.domain.User;
import com.muou.usercenter.model.request.UserLoginRequest;
import com.muou.usercenter.model.request.UserRegisterRequest;
import com.muou.usercenter.model.request.UserSearchRequest;
import com.muou.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.muou.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.muou.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author mccc
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数不可为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数不可为空");
        }
        long result =  userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数不可为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数不可为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数为空");
        }
        Integer result =  userService.userLogout(request);
        return ResultUtils.success(result);
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCodeEnum.NO_LOGIN,"用户未登录");
        }
        // TODO 校验用户是否合法
        User user = userService.getById(currentUser.getId());
        User result = userService.getSafetyUser(user);
        if (null == result) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXIST,"无当前用户信息");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestBody UserSearchRequest search, HttpServletRequest request) {
        // 仅管理员可查询
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH,"用户无权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(search.getUsername())) {
            queryWrapper.like("username", search.getUsername());
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result =  userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 仅管理员可删除
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH,"用户无权限");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXIST,"用户不存在");
        }
        //调用mybatis的删除接口，开启逻辑删除配置（会将删除转变为更新）
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 验证是否为管理员
     *
     * @param request HTTP请求
     * @return 布尔变量
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
