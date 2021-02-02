package org.verwandlung.voj.web.util.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解，用于标识方法是否需要使用缓存
 */
@Target({ElementType.PARAMETER, ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedCacheAop {
    //过期时间
    int time() default 30*60;
}