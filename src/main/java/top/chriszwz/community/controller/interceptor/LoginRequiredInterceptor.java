package top.chriszwz.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.chriszwz.community.annotation.LoginRequired;
import top.chriszwz.community.util.HostHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/*
 * @Description: 登录请求拦截器
 * @Author: Chris(张文卓)
 * @Date: 2022/6/27 20:02
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;//用户信息维护类

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){//判断是否是HandlerMethod
            HandlerMethod handlerMethod = (HandlerMethod) handler;//获取方法上的注解
            Method method = handlerMethod.getMethod();//获取方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//筛选带注解的方法进行拦截
            if(loginRequired != null && hostHolder.getUser() == null){
                //如果用户没有登录，并且请求的方法需要登录，则重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
