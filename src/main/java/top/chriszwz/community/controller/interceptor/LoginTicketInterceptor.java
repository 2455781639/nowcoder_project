package top.chriszwz.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.chriszwz.community.entity.LoginTicket;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.MessageService;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CookieUtil;
import top.chriszwz.community.util.HostHolder;

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

    @Autowired
    private MessageService messageService;

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
                //构建用户认证的结果，并存入SecurityContext,以便于Security进行授权.
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), userService.getAuthorities(user.getId()));
                //存入SecurityContext
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            //查询未读消息数量
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("UnreadCount", letterUnreadCount + noticeUnreadCount);
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}
