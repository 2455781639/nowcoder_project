package top.chriszwz.community.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import top.chriszwz.community.util.CommunityUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * @Description: 异常处理类
 * @Author: Chris(张文卓)
 * @Date: 2022/6/30 20:40
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.error("发生异常：{}", e.getMessage());
        for(StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestWith = request.getHeader("x-requested-with");
        if(xRequestWith != null && xRequestWith.equalsIgnoreCase("XMLHttpRequest")) {
            // 异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));
        } else {
            // 页面请求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
