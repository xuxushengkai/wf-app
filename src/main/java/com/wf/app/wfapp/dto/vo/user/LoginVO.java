package com.wf.app.wfapp.dto.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ApiModel(description = "登录请求信息")
@Getter
@Setter
public class LoginVO {
    @NotEmpty(message = "账号不能为空")
    @ApiModelProperty(notes = "账号",required = true)
    private String account;

    @NotEmpty(message = "密码不能为空")
    @Size(max = 30,message = "密码最多20位")
    @ApiModelProperty(notes = "密码",required =true)
    private String pwd;

}
