package com.wf.app.wfapp.lock;

import com.wf.app.wfapp.annotation.CacheLock;
import com.wf.app.wfapp.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * lock生成规则;
 *
 * @author : zhanglei
 * @description
 * @date : 2019/3/26 19:32
 */
@Component
@Slf4j
public class LockKeyGenerator implements CacheKeyGenerator {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        final Object[] args = pjp.getArgs();
        return getLockName(method, args);
    }

    /**
     * 根据配置的信息生成锁的值。
     * 1，当参数是包含成员属性的对象或者对象集合时，需要指定propertity配置信息，获取对象的成员变量值生成锁的值;
     * 2,当参数不是包含成员属性的对象或者对象集合时，无需指定propertity配置信息，直接用请求参数的值生成锁的值;
     * @param method
     * @param args
     * @return
     */
    public String getLockName(Method method, Object[] args) {
        CacheLock annotation = method.getAnnotation(CacheLock.class);
        StringBuilder stringBuilder = new StringBuilder();
        String lockName = annotation.prefix();
        boolean lockIsToken = annotation.lockIsToken();
        //指定登录token作为锁标识
        if (lockIsToken) {
            stringBuilder.append(annotation.delimiter()).append(jwtTokenUtil.getToken());
        } else if (args != null && args.length > 0 && annotation.argNum().length > 0) {
            //是否指定了对象的某些成员变量
            boolean flag = annotation.propertity() != null && annotation.propertity().length > 0;
            for (int argNum : annotation.argNum()) {
                Object arg = args[argNum - 1];
                if (flag) {
                    for (String propertity : annotation.propertity()) {
                        if (arg instanceof List) {
                            List temp = (List) arg;
                            for (Object object : temp) {
                                Object vl = getProperty(object, propertity);
                                if (vl != null) {
                                    stringBuilder.append(annotation.delimiter()).append(String.valueOf(vl));
                                }
                            }
                        } else {
                            Object vl = getProperty(arg, propertity);
                            if (vl != null) {
                                stringBuilder.append(annotation.delimiter()).append(String.valueOf(vl));
                            }
                        }
                    }
                } else if (arg instanceof List) {
                    List temp = (List) arg;
                    for (Object object : temp) {
                        stringBuilder.append(annotation.delimiter()).append(object);
                    }
                } else {
                    stringBuilder.append(annotation.delimiter()).append(arg);
                }
            }
        }
        return lockName + stringBuilder.toString();
    }

    /**
     * 从对象中过去成员变量的值
     *
     * @param bean 对象
     * @param field   对象的成员变量
     * @return
     */
    public Object getProperty(Object bean, String field) {
        if (!StringUtils.isEmpty(field) && bean != null) {
            try {
                return PropertyUtils.getProperty(bean, field);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(bean + "没有属性" + field + "或未实现get方法。", e);
            } catch (Exception e) {
                throw new RuntimeException("", e);
            }
        }
        return null;
    }

}
