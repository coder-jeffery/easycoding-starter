package com.easycoding.kylin.starter.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author yingzhou.wei
 * @Description redis config
 * @date 2019/1/7
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfig {

    private RedisProperties redisProperties;

    public RedisAutoConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JsonJacksonCodec jsonJacksonCodec() {
        return new JsonJacksonCodec();
    }

    @Bean(name = "redissonClient")
    public RedissonClient getRedission(JsonJacksonCodec jsonJacksonCodec) {
        String address = Arrays.stream(redisProperties.getAddress().split(",")).map(add -> {
            if (!add.startsWith("redis://")) {
                add = "redis://" + add;
            }
            return add;
        }).collect(Collectors.joining(","));
        if (!StringUtils.isEmpty(address) && address.indexOf(",") > 0) {
            return getMultiRedis(address.split(","), jsonJacksonCodec);
        } else {
            return getSingleRedis(address, jsonJacksonCodec);
        }
    }

    // 单点 redis
    private RedissonClient getSingleRedis(String address, JsonJacksonCodec jsonJacksonCodec) {
        Config config = new Config();
        config.setCodec(jsonJacksonCodec);
        SingleServerConfig singleServerConfig = config.useSingleServer();
        int poolSize = Integer.parseInt(redisProperties.getPoolSize());
        if (poolSize < 64) {
            poolSize = 64;
        }
        singleServerConfig.setAddress(address)
                .setDatabase(Integer.parseInt(redisProperties.getDatabase())).setConnectionPoolSize(poolSize);
        if (!StringUtils.isEmpty(redisProperties.getPassword())) {
            singleServerConfig.setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

    // 集群redis
    private RedissonClient getMultiRedis(String[] addresses, JsonJacksonCodec jsonJacksonCodec) {
        Config config = new Config();
        config.setCodec(jsonJacksonCodec);
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        clusterServersConfig.setScanInterval(2000).addNodeAddress(addresses)
                .setMasterConnectionPoolSize(Integer.parseInt(redisProperties.getPoolSize()));
        if (!StringUtils.isEmpty(redisProperties.getPassword())) {
            clusterServersConfig.setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

}
