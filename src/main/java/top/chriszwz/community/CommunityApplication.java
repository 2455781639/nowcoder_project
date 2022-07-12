package top.chriszwz.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/*
 * @Description: 启动类
 * @Author: Chris(张文卓)
 * @Date: 2022/5/22 9:07
 */
@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init() {
        // 解决netty启动冲突,Netty4Util.java
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
