package top.chriszwz.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.chriszwz.community.util.CommunityUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* 
 * @Description: 用于测试url
 * @Author: Chris(张文卓)
 * @Date: 2022/6/24 9:23s
 */
@Controller
public class TestController {
    
//    @RequestMapping(path = "cookie/set",method = RequestMethod.GET)
//    @ResponseBody
//    public String setCookie(HttpServletResponse response){
//        Cookie setCookie = new Cookie("code", CommunityUtil.generateUUID());
//        setCookie.setMaxAge(60 * 10);
//        setCookie.setPath("/");
//        response.addCookie(setCookie);
//        return "set Cookie";
//    }
//
//    @RequestMapping(path = "cookie/get",method = RequestMethod.GET)
//    @ResponseBody
//    public String getCookie(@CookieValue("code")String code){
//        System.out.println(code);
//        return "get Cookie";
//    }
//
//    @RequestMapping(path = "session/set",method = RequestMethod.GET)
//    @ResponseBody
//    public String setSession(HttpSession session){
//        session.setAttribute("id",1);
//        session.setAttribute("name","Chris");
//        return "set Session";
//    }
//
//    @RequestMapping(path = "session/get",method = RequestMethod.GET)
//    @ResponseBody
//    public String getSession(HttpSession session){
//        session.getAttribute("id");
//        session.getAttribute("name");
//        return "get Session";
//    }
}
