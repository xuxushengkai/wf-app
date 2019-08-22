package com.wf.app.wfapp.dto.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(description = "登录请求信息")
@Getter
@Setter
public class LoginVO {
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "\\d{11}",message = "手机号格式有误")
    @Size(min = 11,max = 11,message = "手机号长度必须为11位")
    @ApiModelProperty(notes = "手机号",required = true)
    private String telephone;

    @NotEmpty(message = "密码不能为空")
    @Size(max = 30,message = "密码最多30位")
    @ApiModelProperty(notes = "密码",required =true)
    private String password;

}
