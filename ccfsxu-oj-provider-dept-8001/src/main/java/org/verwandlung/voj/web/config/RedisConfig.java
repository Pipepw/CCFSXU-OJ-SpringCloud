package org.verwandlung.voj.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Configuration
    @Service
    public class RedisService extends CachingConfigurerSupport {

        Logger logger = LoggerFactory.getLogger(RedisService.class);

        @Value("${redis.host}")
        private String host;

        @Value("${redis.port}")
        private int port;

        @Value("${redis.timeout}")
        private int timeout;

        @Bean
        public JedisPool redisPoolFactory() {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, null);
            return jedisPool;
        }
    }
}
