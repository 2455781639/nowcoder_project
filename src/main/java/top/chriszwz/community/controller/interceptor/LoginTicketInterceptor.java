package top.chriszwz.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.chriszwz.community.entity.LoginTicket;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CookieUtil;
import top.chriszwz.community.util.HostHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/*
 * @Description: 登录拦截器
 * @Author: Chris(张文卓)
 * @Date: 2022/6/25 9:09
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从Cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");

        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findById(loginTicket.getUser_id());
                //在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
