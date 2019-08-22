package com.wf.app.wfapp.controller;

import com.wf.app.wfapp.annotation.Login;
import com.wf.app.wfapp.dto.vo.user.LoginResultVO;
import com.wf.app.wfapp.dto.vo.user.LoginVO;
import com.wf.app.wfapp.lock.CacheLock;
import com.wf.app.wfapp.service.UserService;
import com.wf.app.wfapp.service.common.RedisService;
import com.wf.common.constants.ResultCode;
import com.wf.common.controller.BaseController;
import com.wf.common.vo.ResultMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Api(value = "人员接口", description = "人员接口")
@RequestMapping("/test")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;


    @ApiOperation(value = "账户登录", notes = "账户登录")
    @PostMapping("login")
    public ResultMessage login(@Valid @RequestBody LoginVO loginVO, BindingResult bindingResult, HttpServletRequest request) {
        if(null == redisService.get(loginVO.getAccount())){
            //当前用户没有登录
            redisService.setNX(loginVO.getAccount(),"login");
            paramsValid(bindingResult);
            try{
                LoginResultVO loginResultVO = userService.login(loginVO);
                return ResultMessage.success(loginResultVO);
            }catch (Exception e){
                //登录失败
                redisService.del(loginVO.getAccount());
                return ResultMessage.fail(e.getMessage());
            }
        }else{
            return ResultMessage.fail(ResultCode.ACCOUNT_IS_LOGIN.getCode(),ResultCode.ACCOUNT_IS_LOGIN.getMessage());
        }
        // 记录日志
//        UserLoginLogVO userLoginLogVO = new UserLoginLogVO();
//        BeanUtils.copyProperties(loginResultVO,userLoginLogVO);
//        userLoginLogVO.setLoginIp(IPUtils.getRemoteIpAddr(request));
//        userLoginLogVO.setLoginTime(LocalDateTime.now());
//        userLoginLogVO.setMethod("login");
    }

    @Login
    @ApiOperation(value = "人员详情", notes = "人员详情")
    @GetMapping("/getList")
    public ResultMessage list(){
        return ResultMessage.success(userService.findAll());
    }

}
