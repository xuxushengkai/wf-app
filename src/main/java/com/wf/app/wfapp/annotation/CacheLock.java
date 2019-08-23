package com.wf.app.wfapp.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

    /**
     * redis 锁key的前缀
     *
     * @return redis 锁key的前缀
     */
    String prefix() default "";

    /**
     * 过期秒数,默认为10秒
     *
     * @return 轮询锁的时间
     */
    int expire() default 10;

    /**
     * 超时时间单位
     *
     * @return 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * <p>Key的分隔符（默认 :）</p>
     * <p>生成的Key：N:SO1008:500</p>
     *
     * @return String
     */
    String delimiter() default ":";

    /**
     * 当不需要细粒度来控制锁时，可以设置该值为true,直接用登录token作为锁。
     */
    boolean lockIsToken() default false;

    /**
     * 当需要细粒度来控制锁时，
     * 将方法第argNum个参数来构建锁的值。
     * 1，当propertity为空时，可以通过argNum参数直接构建锁的值。
     * 2，当propertity不为空时，可以通过获取argNum参数对象包含的成员属性来构建锁的值。
     */
    int [] argNum() default {1};


    /**
     * <pre>
     *     当需要细粒度来控制锁时,请先指定argNum,再指定propertity。
     *     因为有时候lockName是不固定的,获取注解的方法参数列表的某个参数对象的某个属性值来作为lockName。
     *     1，当参数是包含成员属性的对象或者对象集合时，需要指定propertity信息。
     *     2,当参数不是包含成员属性的对象或者对象集合时，不要指定propertity值。
     *     当propertity不为空时，可以通过argNum参数来设置具体是参数列表的第几个参数，不设置则默认取第一个。
     * </pre>
     */
    String [] propertity() default {};
}
