package com.wf.app.wfapp.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.app.wfapp.common.Constants;
import com.wf.app.wfapp.dao.UserMapper;
import com.wf.app.wfapp.dto.entity.UserEntity;
import com.wf.app.wfapp.dto.vo.user.*;
import com.wf.app.wfapp.service.common.RedisService;
import com.wf.app.wfapp.util.JwtTokenUtil;
import com.wf.common.constants.ResultCode;
import com.wf.common.exception.WFException;
import com.wf.common.utils.DesEncryption;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisService redisService;

    /**
     * @return com.wf.app.wfapp.dto.vo.user.LoginResultVO
     * @Description //登录
     * @Date 10:57 2019/8/23
     * @Param [loginVO]
     **/
    public LoginResultVO login(LoginVO loginVO) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getAccount, loginVO.getAccount());
        UserEntity userEntity = getOne(queryWrapper);
        if (userEntity == null) {
            throw new WFException(ResultCode.USER_NOT_EXIST);
        } else if (userEntity.getStatus().equals(Constants.Status.DISABLE.code)) {
            throw new WFException(ResultCode.USER_NOT_EXIST);
        }
        if (!userEntity.getPwd().equals(DesEncryption.generatePassword(userEntity.getId(), loginVO.getPwd()))) {
            throw new WFException(ResultCode.PASSWORD_ERROR);
        }
        LoginResultVO resultVO = new LoginResultVO();
        BeanUtils.copyProperties(userEntity, resultVO);
        String token = jwtTokenUtil.generateToken(new JWTInfo(userEntity.getId(), loginVO.getAccount(), System.currentTimeMillis() + ""));
        resultVO.setToken(token);
        resultVO.setUserId(userEntity.getId());
        resultVO.setLoginTime(LocalDateTime.now());
        redisService.set(token, JSON.toJSONString(resultVO), jwtTokenUtil.getJwtTokenExpire());
        return resultVO;
    }

    /**
     * @return com.wf.app.wfapp.dto.vo.user.LoginResultVO
     * @Description //登出
     * @Date 10:57 2019/8/23
     * @Param [loginVO]
     **/
    public void logout() {
        String token = jwtTokenUtil.getToken();
        if (StringUtils.isNotEmpty(token)) {
            redisService.del(jwtTokenUtil.getLoginUserFromToken().getAccount());
            redisService.del(token);
        }
    }

    /**
     * @return java.util.List<com.wf.app.wfapp.dto.entity.UserEntity>
     * @Description //详情
     * @Date 13:21 2019/8/23
     * @Param [id]
     **/
    public UserVO getDetail(String id) {
        UserEntity entity = this.getById(id);
        if(null == entity){
            throw new WFException(ResultCode.USER_NOT_EXIST);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(entity,userVO);
        return userVO;
    }
}
