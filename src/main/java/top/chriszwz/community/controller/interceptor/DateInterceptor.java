package top.chriszwz.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.DateService;
import top.chriszwz.community.util.HostHolder;

@Configuration
public class DateInterceptor implements HandlerInterceptor {

    @Autowired
    private DateService dateService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dateService.recordUV(ip);

        //统计DAU
        User user = hostHolder.getUser();
        if(user != null){
            dateService.recordDAU(user.getId());
        }
        return true;
    }

}
