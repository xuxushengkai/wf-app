package com.wf.app.wfapp.controller;

import com.wf.app.wfapp.annotation.CacheLock;
import com.wf.app.wfapp.annotation.Login;
import com.wf.app.wfapp.controller.basic.BaseController;
import com.wf.app.wfapp.dto.entity.UserLoginRecordEntity;
import com.wf.app.wfapp.dto.vo.user.LoginResultCacheVO;
import com.wf.app.wfapp.dto.vo.user.LoginResultVO;
import com.wf.app.wfapp.dto.vo.user.LoginVO;
import com.wf.app.wfapp.service.UserLoginRecordService;
import com.wf.app.wfapp.service.UserService;
import com.wf.common.constants.ResultCode;
import com.wf.common.exception.WFException;
import com.wf.common.vo.ResultMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RestController
@Api(value = "人员接口", description = "人员接口")
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserLoginRecordService recordService;


    @ApiOperation(value = "账户登录", notes = "账户登录")
    @PostMapping("login")
    @CacheLock(prefix = "login",propertity = {"account"},expire = 3)
    public ResultMessage login(@Valid @RequestBody LoginVO loginVO, BindingResult bindingResult) {
        if(null == redisService.get(loginVO.getAccount())){
            //当前用户没有登录
            redisService.setNX(loginVO.getAccount(),"login", jwtTokenUtil.getJwtTokenExpire());
            paramsValid(bindingResult);
            try{
                LoginResultVO loginResultVO = userService.login(loginVO);
                // 记录日志
                UserLoginRecordEntity record = new UserLoginRecordEntity();
                BeanUtils.copyProperties(loginResultVO, record);
                record.setLoginTime(loginResultVO.getLoginTime());
                recordService.addLog(record);
                return ResultMessage.success(loginResultVO);
            }catch (WFException e){
                //登录失败
                redisService.del(loginVO.getAccount());
                return ResultMessage.fail(e.getResultCode().getMessage());
            }
        }else{
            return ResultMessage.fail(ResultCode.ACCOUNT_IS_LOGIN.getCode(),ResultCode.ACCOUNT_IS_LOGIN.getMessage());
        }
    }

    @Login
    @ApiOperation(value = "用户登出", notes = "用户登出")
    @PostMapping("logout")
    public ResultMessage logout() {
        // 记录日志
        UserLoginRecordEntity record = new UserLoginRecordEntity();
        LoginResultCacheVO loginResultCacheVO = jwtTokenUtil.getLoginUserFromToken();
        BeanUtils.copyProperties(loginResultCacheVO,record);
        record.setToken(loginResultCacheVO.getToken());
        record.setLoginTime(Instant.ofEpochMilli(Long.parseLong(loginResultCacheVO.getLoginTime())).atZone(ZoneOffset.ofHours(8)).toLocalDateTime());
        record.setLogoutTime(LocalDateTime.now());
        recordService.addLog(record);
        //登出
        userService.logout();
        return ResultMessage.success();
    }

    @Login
    @ApiOperation(value = "人员详情", notes = "人员详情")
    @GetMapping("/detail")
    public ResultMessage getDetail(@RequestParam String id){
        return ResultMessage.success(userService.getDetail(id));
    }

}
