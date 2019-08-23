package com.wf.app.wfapp.dto.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.common.po.BaseEntityPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "tb_user_login_record")
public class UserLoginRecordEntity extends BaseEntityPo {

    @TableField(value = "user_id")
    private String userId;

    private String name;

    private String account;

    @TableField(value = "login_time")
    private LocalDateTime loginTime;

    @TableField(value = "logout_time")
    private String logoutTime;

    private String token;
}
