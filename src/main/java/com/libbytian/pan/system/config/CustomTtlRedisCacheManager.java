package com.libbytian.pan.system.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * @author jiangli
 * @since 2021/1/12 15:07
 * 通过cacheName自定义过期时间的RedisCacheManager
 * 支持直接使用cacheName来定义过期时间
 * cacheName：name#time   time为过期时间，单位秒，0为不过期
 * 例如：test#100  意思是定义一个名为test#100的缓存，且过期时间为100秒
 */
public class CustomTtlRedisCacheManager extends RedisCacheManager {

    public CustomTtlRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        Duration ttl = getTtlByName(name);
        if (ttl != null) {
            //证明在cacheName上使用了过期时间，需要修改配置中的ttl
            cacheConfig = cacheConfig.entryTtl(ttl);
        }
        //修改缓存key和value值的序列化方式
        cacheConfig = cacheConfig.computePrefixWith(DEFAULT_CACHE_KEY_PREFIX)
                .serializeValuesWith(DEFAULT_PAIR);
        return super.createRedisCache(name, cacheConfig);
    }

    /**
     * 缓存参数的分隔符
     * 数组元素0=缓存的名称
     * 数组元素1=缓存过期时间TTL
     */
    private static final String DEFAULT_SEPARATOR = "#";

    /**
     * 通过name获取过期时间
     *
     * @param name
     * @return
     */
    private Duration getTtlByName(String name) {
        if (name == null) {
            return null;
        }
        //根据分隔符拆分字符串，并进行过期时间ttl的解析
        String[] cacheParams = name.split(DEFAULT_SEPARATOR);
        if (cacheParams.length > 1) {
            String ttl = cacheParams[1];
            if (!StrUtil.isEmpty(ttl)) {
                try {
                    return Duration.ofSeconds(Long.parseLong(ttl));
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * 默认的key前缀
     */
    private static final CacheKeyPrefix DEFAULT_CACHE_KEY_PREFIX = cacheName -> cacheName + ":";

    /**
     * 默认序列化方式为json
     */
    private static final RedisSerializationContext.SerializationPair<Object> DEFAULT_PAIR = RedisSerializationContext.SerializationPair
            .fromSerializer(new GenericJackson2JsonRedisSerializer());
}