package top.chriszwz.community.controller;

import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.service.UserService;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;
import top.chriszwz.community.util.RedisKeyUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * @Description: 登录注册类
 * @Author: Chris(张文卓)
 * @Date: 2022/5/22 21:50
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    //进入注册页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    //进入登录页面
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //注册
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }


    @RequestMapping(path = "/activation/{user_id}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("user_id") int user_id, @PathVariable("code")String code){
        int activation = userService.activation(user_id, code);
        if(activation == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target","/login");
        } else if (activation == ACTIVATION_REPEAT) {
            model.addAttribute("msg","无效操作，该账号已被激活!");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，激活码不正确!");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
//        session.setAttribute("kaptcha",text);

        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);//过期时间
        cookie.setPath("/");//设置路径
        response.addCookie(cookie);
        //将验证码存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        //将图片输出到浏览器
        response.setContentType("image/png");
        try {
            //输出流将图片信息保存
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    //登录
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, HttpServletResponse response, @CookieValue("kaptchaOwner")String kaptchaOwner) {
        //验证验证码
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha  = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);

        }

        if (!kaptcha.equalsIgnoreCase(code) || StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        //验证用户名密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            //登录成功
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);//添加cookie到响应中
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //注销
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

}