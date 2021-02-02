package org.verwandlung.voj.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.verwandlung.voj.web.aspect.RedisAspect;

@EnableAspectJAutoProxy
@Configuration
public class AspectConfig {

    @Bean
    public RedisAspect redisAspect(){
        return new RedisAspect();
    }
}
