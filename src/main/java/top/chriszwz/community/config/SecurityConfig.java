package top.chriszwz.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "user/setting",//用户设置页面
                        "/user/upload",//用户头像上传
                        "/discuss/add",//添加评论
                        "/comment/add/**",//添加评论
                        "/letter/**",//发送私信
                        "/notice/**",//发送通知
                        "/like",//点赞
                        "/follow",//关注
                        "/unfollow"//取消关注
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",//置顶评论
                        "/discuss/wonderful"//加精评论
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR//版主
                )
                .antMatchers(
                        "/discuss/delete"//删除评论
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()//其他请求都允许匿名访问
                .and().csrf().disable();//关闭csrf防护

        // 权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登陆时的处理
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("X-Requested-With");//判断是否是ajax请求
                        if (xRequestedWith != null && xRequestedWith.equalsIgnoreCase("XMLHttpRequest")) {//异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "您还没有登陆，请先登陆"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //没有权限时的处理
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("X-Requested-With");//判断是否是ajax请求
                        if (xRequestedWith != null && xRequestedWith.equalsIgnoreCase("XMLHttpRequest")) {//异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "您还没有权限操作"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层默认拦截/logout请求，所以需要指定一个退出的请求路径
        // 覆盖它默认的逻辑，才能执行我们自定义的退出逻辑
        http.logout().logoutUrl("/securitylogout");//覆盖推出路径(假路径)
    }
}
