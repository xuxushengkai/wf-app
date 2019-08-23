package com.wf.app.wfapp.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.app.wfapp.dao.UserLoginRecordMapper;
import com.wf.app.wfapp.dto.entity.UserLoginRecordEntity;
import org.springframework.stereotype.Service;

@Service
public class UserLoginRecordService extends ServiceImpl<UserLoginRecordMapper, UserLoginRecordEntity> {

    public void addLog(UserLoginRecordEntity entity){
        this.saveOrUpdate(entity);
    }
}
