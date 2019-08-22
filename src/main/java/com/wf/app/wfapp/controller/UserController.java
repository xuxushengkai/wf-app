package com.wf.app.wfapp.controller;

import com.wf.app.wfapp.annotation.Login;
import com.wf.app.wfapp.dto.vo.user.LoginResultVO;
import com.wf.app.wfapp.dto.vo.user.LoginVO;
import com.wf.app.wfapp.service.UserService;
import com.wf.common.controller.BaseController;
import com.wf.common.vo.ResultMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@Api(value = "人员接口", description = "人员接口")
@RequestMapping("/test")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "账户登录", notes = "账户登录")
    @PostMapping("login")
    public ResultMessage login(@Valid @RequestBody LoginVO loginVO, BindingResult bindingResult, HttpServletRequest request) {
        paramsValid(bindingResult);
        LoginResultVO loginResultVO = userService.login(loginVO);
        // 记录日志
//        UserLoginLogVO userLoginLogVO = new UserLoginLogVO();
//        BeanUtils.copyProperties(loginResultVO,userLoginLogVO);
//        userLoginLogVO.setLoginIp(IPUtils.getRemoteIpAddr(request));
//        userLoginLogVO.setLoginTime(LocalDateTime.now());
//        userLoginLogVO.setMethod("login");
        return ResultMessage.success(loginResultVO);
    }

    @Login
    @ApiOperation(value = "人员列表", notes = "人员列表")
    @GetMapping("/getList")
    public ResultMessage list(){
        return ResultMessage.success(userService.findAll());
    }

}
