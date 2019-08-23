package com.wf.app.wfapp.aspect;

import com.alibaba.fastjson.JSON;
import com.wf.app.wfapp.annotation.Login;
import com.wf.app.wfapp.dto.vo.user.LoginResultCacheVO;
import com.wf.app.wfapp.lock.CacheKeyGenerator;
import com.wf.app.wfapp.annotation.CacheLock;
import com.wf.app.wfapp.lock.RedisLockHelper;
import com.wf.app.wfapp.util.JwtTokenUtil;
import com.wf.common.constants.ResultCode;
import com.wf.common.vo.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Aspect
@Order(1)
@Slf4j
public class RequestAspect {
    @Autowired
    private RedisLockHelper redisLockHelper;
    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public static InheritableThreadLocal<LoginResultCacheVO> inheritableThreadLocal = new InheritableThreadLocal<>();


    @Pointcut("execution(public * com.wf.app.wfapp.controller..*.*(..))")
    public void controllerAspect() {
        log.info("controllerAspect");
    }

    @Before(value = "controllerAspect()")
    public void before(JoinPoint joinPoint) {
        setThreadLocal(joinPoint);
    }

    @Around(value = "controllerAspect()")
    public Object methodAround(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true); // 子线程共享

        Instant startTime = Instant.now();

        StringBuilder params = new StringBuilder();
        Object[] args = pjp.getArgs();
        List<Object> logArgs = streamOf(args)
                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse) && !(arg instanceof BindingResult) && !(arg instanceof MultipartFile)))
                .collect(Collectors.toList());

        for (Object arg : logArgs) {
            params.append(JSON.toJSONString(arg)).append(", ");
        }


        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock cacheLock = method.getAnnotation(CacheLock.class);
        Object result;
        if (cacheLock == null) {
            result = pjp.proceed();
        } else {
            result = requestValidate(pjp);
        }
        removeThreadLocal(pjp);
        //打印返回内容
        if (result instanceof ResultMessage) {
            ResultMessage resultMessage = (ResultMessage) result;
            log.info("Response内容: code: {}, message: {}, Data: {}", resultMessage.getCode(), resultMessage.getMessage(), JSON.toJSONString(resultMessage.getData()));
        }
        log.info("执行时间：{}毫秒", Duration.between(startTime, Instant.now()).toMillis());
        log.info("--------------返回内容----------------");
        return result;
    }

    private static <T> Stream<T> streamOf(T[] array) {
        return ArrayUtils.isEmpty(array) ? Stream.empty() : Arrays.stream(array);
    }

    public Object requestValidate(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock cacheLock = method.getAnnotation(CacheLock.class);
        if (!StringUtils.isEmpty(cacheLock.prefix())) {
            String lockKey = null;
            String value = null;
            boolean success;
            try {
                lockKey = cacheKeyGenerator.getLockKey(pjp);
                value = UUID.randomUUID().toString();
                // 假设上锁成功，但是设置过期时间失效，以后拿到的都是 false
                success = redisLockHelper.lock(lockKey, value, cacheLock.expire(), cacheLock.timeUnit());
                log.info("lock key:[{}],uuid:[{}],set lock result:[{}]", lockKey, value, success);
                if (!success) {
                    return ResultMessage.fail(ResultCode.REPEATED_SUBMISSION_ERROR.getCode(), ResultCode.REPEATED_SUBMISSION_ERROR.getMessage());
                }
                return pjp.proceed();
            } catch (Throwable throwable) {
                log.error("", throwable);
                if (cacheLock != null) {
                    redisLockHelper.unlock(lockKey, value);
                }
                return ResultMessage.fail(ResultCode.SYSTEM_ERROR.getCode(), throwable.getMessage()!=null?throwable.getMessage():ResultCode.SYSTEM_ERROR.getMessage());
            } finally {
                if (lockKey != null) {
                    redisLockHelper.doUnlock(lockKey, value);
                }
            }
        }
        return null;
    }

    private void setThreadLocal(JoinPoint joinPoint) {
        //获取并判断类上是否有注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Login login = signature.getMethod().getDeclaredAnnotation(Login.class);
        //如果有注解的情况
        if (login != null) {
            LoginResultCacheVO loginResultCacheVO = jwtTokenUtil.getLoginUserFromToken();
            inheritableThreadLocal.set(loginResultCacheVO);
        }
    }

    private void removeThreadLocal(ProceedingJoinPoint joinPoint) {
        //获取并判断类上是否有注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Login login = signature.getMethod().getDeclaredAnnotation(Login.class);
        //如果有注解的情况
        if (login != null) {
            inheritableThreadLocal.remove();
        }
    }
}
