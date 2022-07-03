package top.chriszwz.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/*
 * @Description: redis配置类
 * @Author: Chris(张文卓)
 * @Date: 2022/7/2 20:57
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {//Redis工厂
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key序列化方式
        template.setKeySerializer(RedisSerializer.string());

        //设置value序列化方式
        template.setValueSerializer(RedisSerializer.json());

        //设置hash key序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

        //设置hash value序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //设置做完后触发生效
        template.afterPropertiesSet();
        return template;
    }
}
