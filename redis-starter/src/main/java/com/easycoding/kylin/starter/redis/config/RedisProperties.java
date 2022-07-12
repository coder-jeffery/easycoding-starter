package com.easycoding.kylin.starter.redis.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String address;
    private String password;
    private String poolSize = "64";
    private String database = "0";

}
