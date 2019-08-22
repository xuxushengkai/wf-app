package com.wf.app.wfapp.dto.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wf.common.po.BaseEntityPo;
import lombok.Data;

import java.math.BigDecimal;

@TableName(value = "tb_user")
@Data
public class UserEntity extends BaseEntityPo {

    private String account;

    private String pwd;

    private String name;

    private String telephone;

    private String wechat;

    @TableField(value = "account_money")
    private BigDecimal accountMoney;

    private Integer status;
}
