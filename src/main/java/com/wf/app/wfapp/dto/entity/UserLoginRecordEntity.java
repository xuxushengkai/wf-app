package com.wf.app.wfapp.dto.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private LocalDateTime loginTime;

    @TableField(value = "logout_time")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private LocalDateTime logoutTime;

    private String token;
}
