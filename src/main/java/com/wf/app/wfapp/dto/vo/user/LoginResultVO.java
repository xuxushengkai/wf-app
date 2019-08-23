package com.wf.app.wfapp.dto.vo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "登录结果信息")
@Getter
@Setter
public class LoginResultVO {

    @ApiModelProperty(notes = "用户ID")
    private String userId;

    @ApiModelProperty(notes = "用户名称")
    private String name;

    @ApiModelProperty(notes = "账号")
    private String account;

    @ApiModelProperty(notes = "手机号")
    private String telephone;

    @ApiModelProperty(notes = "登录token")
    private String token;

   @JsonIgnore
    private String loginTime;


}
