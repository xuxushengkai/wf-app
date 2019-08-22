package com.wf.app.wfapp.aspect;

import com.wf.app.wfapp.dto.vo.user.JWTInfo;
import com.wf.app.wfapp.service.common.RedisService;
import com.wf.app.wfapp.util.JwtTokenUtil;
import com.wf.common.constants.ResultCode;
import com.wf.common.vo.ResultMessage;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class LoginAspectj {

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Pointcut("@annotation(com.wf.app.wfapp.annotation.Login)")
    public void entrance() {
    }

    @Around(value = "entrance()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String token = jwtTokenUtil.getToken();
        if (StringUtils.isEmpty(token)) {
            return ResultMessage.fail(ResultCode.LOGIN_TOKEN_IS_NULL.getCode(), ResultCode.LOGIN_TOKEN_IS_NULL.getMessage());
        }
        JWTInfo jwtInfo = jwtTokenUtil.getInfoFromToken(token);
        if (jwtInfo == null) {
            return ResultMessage.fail(ResultCode.ACCESS_TOKEN_INVALID.getCode(), ResultCode.ACCESS_TOKEN_INVALID.getMessage());
        }
        int tokenExpire=jwtTokenUtil.getJwtTokenExpire();
        if (!redisService.expire(token, tokenExpire)) {
            redisService.del(jwtTokenUtil.getLoginUserFromToken().getAccount());
            return ResultMessage.fail(ResultCode.LOGIN_TOKEN_EXPIRE.getCode(), ResultCode.LOGIN_TOKEN_EXPIRE.getMessage());
        }
        return pjp.proceed();
    }
}
