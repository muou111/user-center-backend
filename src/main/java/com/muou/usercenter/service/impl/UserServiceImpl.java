package com.muou.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muou.usercenter.common.ErrorCodeEnum;
import com.muou.usercenter.exception.BusinessException;
import com.muou.usercenter.service.UserService;
import com.muou.usercenter.model.domain.User;
import com.muou.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.muou.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
* @author mccc
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "mccc";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "请求参数不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户账号小于四位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户密码过短");
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~! @#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"账号不可包含特殊字符");
        }
        // 密码和确认密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数不可为空");
        }
        // 用户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_IS_EXIST,"该账号名已被注册");
        }

        // 2. 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 向数据库插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR,"注册失败，请重试");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"请求参数不可为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"账号长度过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"密码长度过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[` ~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR,"账号不可包含特殊字符");
        }

        // 2. 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount not match userPassword. userAccount:{}", userAccount);
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXIST,"账号或密码错误，登录失败");
        }

        // 4. 用户脱敏
        User safetyUser = getSafetyUser(user);
        if (safetyUser != null) {
            // 5. 记录用户登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

            return safetyUser;
        }
        else {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR,"登录失败");
        }

    }

    /**
     * 用户脱敏
     *
     * @param originUser 待脱敏用户
     * @return 脱敏后用户
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     *用户注销
     *
     * @param request http请求
     */
    public int userLogout(HttpServletRequest request){
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCodeEnum.NO_LOGIN,"用户未登录");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




