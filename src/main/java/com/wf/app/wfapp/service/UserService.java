package com.wf.app.wfapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.app.wfapp.common.Constants;
import com.wf.app.wfapp.dao.UserMapper;
import com.wf.app.wfapp.dto.entity.UserEntity;
import com.wf.app.wfapp.dto.vo.user.LoginResultVO;
import com.wf.app.wfapp.dto.vo.user.LoginVO;
import com.wf.common.constants.ResultCode;
import com.wf.common.exception.WFException;
import com.wf.common.utils.DesEncryption;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper,UserEntity> {

    public List<UserEntity> findAll(){
        return this.list(null);
    }

    public LoginResultVO login(LoginVO loginVO) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getTelephone, loginVO.getTelephone());
        UserEntity userEntity = getOne(queryWrapper);
        if (userEntity == null) {
            throw new WFException(ResultCode.USER_NOT_EXIST.getMessage(),ResultCode.USER_NOT_EXIST.getCode());
        } else if (userEntity.getStatus() .equals(Constants.Status.DISABLE.code)) {
            throw new WFException(ResultCode.USER_NOT_EXIST.getMessage(),ResultCode.USER_NOT_EXIST.getCode());
        }
        if (!userEntity.getPwd().equals(DesEncryption.generatePassword(userEntity.getId(), loginVO.getPassword()))) {
            throw new WFException(ResultCode.PASSWORD_ERROR);
        }
        LoginResultVO resultVO = new LoginResultVO();
        BeanUtils.copyProperties(userEntity,resultVO);
        return resultVO;
    }
}
