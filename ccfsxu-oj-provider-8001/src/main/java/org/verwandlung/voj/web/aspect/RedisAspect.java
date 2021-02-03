package org.verwandlung.voj.web.aspect;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.verwandlung.voj.web.util.annotation.NeedCacheAop;
import org.verwandlung.voj.web.service.RedisService;
import org.verwandlung.voj.web.util.SerializeUtil;

@Aspect
public class RedisAspect {

    @Autowired
    RedisService CacheService;

    /**设置切入点*/
    //方法上面有@NeedCacheAop的方法,增加灵活性
    @Pointcut("@annotation(org.verwandlung.voj.web.util.annotation.NeedCacheAop)")
    public void annotationAspect(){}

    /**环绕通知*/
    @Around(value = "annotationAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint){
        //存储接口返回值
        Object object = new Object();

        //获取注解对应配置过期时间
        NeedCacheAop cacheAop = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(NeedCacheAop.class);  //获取注解自身
        int time;
        if (cacheAop == null){//规避使用第二种切点进行缓存操作的情况
            time = 30*60;  //默认过期时间为30分钟
        }else{
            time = cacheAop.time();
        }
        //获取key
        String key = CacheService.getKeyForAop(joinPoint);
        if (CacheService.containKey(key)){
            String obj = CacheService.get(key);
            if ("fail".endsWith(obj)){  //规避redis服务不可用
                try {
                    //执行接口调用的方法
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }else{
                JSONObject klass =  SerializeUtil.unserializeObject(obj);
                return new ResponseEntity<>(klass.get("body"), HttpStatus.OK) ;
            }
        }else{
            try {
                ////执行接口调用的方法并获取返回值
                object = joinPoint.proceed();
                String serobj = SerializeUtil.serializeObject(object);
                CacheService.set(key,serobj,time);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return object;
    }
}
