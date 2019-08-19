package com.wf.app.wfapp.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.app.wfapp.dao.UserMapper;
import com.wf.app.wfapp.dto.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper,UserEntity> {

    public List<UserEntity> findAll(){
        System.out.println("11111");
        return this.list(null);
    }
}
