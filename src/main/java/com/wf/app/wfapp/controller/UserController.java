package com.wf.app.wfapp.controller;

import com.wf.app.wfapp.dto.vo.ResultMessage;
import com.wf.app.wfapp.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "人员接口", description = "人员接口")
@RequestMapping("/test")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "人员列表", notes = "人员列表")
    @GetMapping("/getList")
    public ResultMessage list(){
        return ResultMessage.success(userService.findAll());
    }

}
