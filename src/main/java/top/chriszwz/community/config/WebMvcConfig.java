package top.chriszwz.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.chriszwz.community.controller.interceptor.AlphaInterceptor;
import top.chriszwz.community.controller.interceptor.DateInterceptor;
import top.chriszwz.community.controller.interceptor.LoginTicketInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;//登陆凭证拦截器

//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private DateInterceptor dateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/static/**")//不需要拦截的路径
                .addPathPatterns("/login", "/register");//需要拦截的路径

        registry.addInterceptor(loginTicketInterceptor)//登陆凭证拦截器
                .excludePathPatterns("/static/**");//不需要拦截的路径

//        registry.addInterceptor(loginRequiredInterceptor)
//                .addPathPatterns("/discuss/detail/**")//需要拦截的路径
//                .excludePathPatterns("/static/**");//不需要拦截的路径
        registry.addInterceptor(dateInterceptor)//日期拦截器
                .excludePathPatterns("/static/**");//不需要拦截的路径

    }
}
