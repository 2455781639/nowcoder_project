package top.chriszwz.community.config;


import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/*
 * @Description: 验证码配置类
 * @Author: Chris(张文卓)
 * @Date: 2022/6/25 8:35
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
        //图像宽度
        properties.setProperty("kaptcha.image.width","100");
        //图像高度
        properties.setProperty("kaptcha.image.height","40");
        //验证码字体大小
        properties.setProperty("kaptcha.textproducer.font.size","32");
        //验证码字体颜色
        properties.setProperty("kaptcha.textproducer.font.color","black");
        //验证码字体取值范围
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        //验证码长度
        properties.setProperty("kaptcha.textproducer.char.length","4");
        //实现类
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
