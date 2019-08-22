package com.wf.app.wfapp.dto.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@ApiModel(description = "登录结果缓存信息")
@Getter
@Setter
public class LoginResultCacheVO implements Serializable {

    //要定义序列化编号，否则在不同机器上JVM自身可能会生成不同的serialVersionUID，导致出现反序列化失败的问题
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "用户ID")
    private String userId;

    @ApiModelProperty(notes = "用户名称")
    private String userName;

    @ApiModelProperty(notes = "用户登录时间")
    private Long loginTime;

}
