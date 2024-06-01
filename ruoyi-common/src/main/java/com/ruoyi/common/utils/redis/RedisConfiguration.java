package com.ruoyi.common.utils.redis;

/**
 * @author JX
 * @create 2024-03-05 11:29
 * @dedescription:
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

/**
 * @author vinjcent
 * 配置redis序列化json
 */
@Configuration
public class RedisConfiguration {


    @Bean
    /**
     * 若有相同类型的Bean时,优先使用此注解标注的Bean
     */
    @Primary
    public RedisTemplate<String, Object> ruoyiRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 为了开发方便,一般直接使用<String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 配置具体的序列化方式
        // JSON解析任意对象
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域field,get和set,以及修饰符范围,ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型,类必须是非final修饰的,final修饰的类,比如String,Integer等会抛出异常
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 设置日期格式
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();


        // key采用String的序列化
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化
        template.setHashKeySerializer(stringRedisSerializer);
        // value的序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 设置所有配置
        template.afterPropertiesSet();

        return template;
    }
}




