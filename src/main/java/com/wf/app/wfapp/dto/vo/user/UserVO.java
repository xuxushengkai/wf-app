package com.wf.app.wfapp.dto.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserVO {

    @ApiModelProperty(notes = "用户ID")
    private String id;

    @ApiModelProperty(notes = "用户名称")
    private String name;

    @ApiModelProperty(notes = "账号")
    private String account;

    @ApiModelProperty(notes = "手机号")
    private String telephone;

    @ApiModelProperty(notes = "微信号")
    private String wechat;
}
