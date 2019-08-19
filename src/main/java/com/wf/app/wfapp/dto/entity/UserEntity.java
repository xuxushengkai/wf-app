package com.wf.app.wfapp.dto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "tb_user")
@Data
public class UserEntity {

    private String id;

    private String account;

    private String pwd;

    private String name;

    private String telephone;

    private String wechat;
}
