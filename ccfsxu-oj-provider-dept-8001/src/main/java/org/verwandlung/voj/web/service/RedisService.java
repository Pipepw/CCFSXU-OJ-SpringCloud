package org.verwandlung.voj.web.service;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Service
public class RedisService {
    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private JedisPool jedisPool;

    /**获取jedis实例*/
    public Jedis getResource() throws Exception{
        return jedisPool.getResource();
    }

    /**设置key与value*/
    public void set(String key, String value, int time) {
        Jedis jedis=null;
        try{
            jedis = getResource();
            jedis.set(key,value);
            jedis.expire(key,time);
        } catch (Exception e) {
            logger.error("Redis set error: "+ e.getMessage() +" - " + key + ", value:" + value);
        }finally{
            returnResource(jedis);
        }
    }

    /**根据key获取value*/
    public String get(String key) {
        String result = null;
        Jedis jedis=null;
        try{
            jedis = getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            logger.error("Redis set error: "+ e.getMessage() +" - " + key + ", value:" + result);
        }finally{
            returnResource(jedis);
        }
        return result;
    }

    /**判断是否存在key*/
    public boolean containKey(String key){
        boolean b;
        Jedis jedis = null;
        try{
            jedis = getResource();
            b = jedis.exists(key);
            return b;
        }catch (Exception e){
            logger.error("Redis server error:："+e.getMessage());
            return false;
        }finally {
            returnResource(jedis);
        }
    }

    /**释放jedis实例资源*/
    public void returnResource(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }

    /**获取key*/
    public String getKeyForAop(JoinPoint joinPoint){

        String applId = null;
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            applId = String.valueOf(args[0]);
        }
        String target = joinPoint.getTarget().toString();
        String className = target.split("@")[0];
        //获取目标方法的方法名称
        String methodName = joinPoint.getSignature().getName();
        //redis中key格式：    applId:方法名称
        String redisKey = applId + ":" + className + "." + methodName;
        return redisKey;
    }
}
