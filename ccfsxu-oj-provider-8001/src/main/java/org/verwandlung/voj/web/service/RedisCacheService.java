package org.verwandlung.voj.web.service;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RedisCacheService implements Cache {

    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

    @Autowired
    private JedisPool jedisPool;

    private final String id;

    /**
     * The {@code ReadWriteLock}
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**获取jedis实例*/
    public RedisCacheService(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        logger.debug("MybatisRedisCache:id=" + id);
        this.id = id;
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
            jedis.flushAll();
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getObject(Object key) {
        Object result = null;
        try (Jedis jedis = jedisPool.getResource()) {
            RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
            result = serializer.deserialize(jedis.get(serializer.serialize(key)));
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    @Override
    public int getSize() {
        int result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            result = Integer.parseInt(jedis.dbSize().toString());
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void putObject(Object key, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
            jedis.set(serializer.serialize(key), serializer.serialize(value));
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object removeObject(Object key) {
        Jedis jedis = null;
        Object result = null;
        try
        {
            jedis = jedisPool.getResource();
            RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
            result =jedis.expire(serializer.serialize(key), 0);
        }
        catch (JedisConnectionException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

}
