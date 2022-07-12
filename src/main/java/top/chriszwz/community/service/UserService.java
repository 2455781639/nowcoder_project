package top.chriszwz.community.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.chriszwz.community.dao.LoginTicketMapper;
import top.chriszwz.community.dao.UserMapper;
import top.chriszwz.community.entity.LoginTicket;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.CommunityUtil;
import top.chriszwz.community.util.MailClient;
import top.chriszwz.community.util.RedisKeyUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    public User findById(int id){
//        return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    //注册
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        if(user == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//取前五位即可
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));//密码加密规则
        user.setType(0);//普通用户:0
        user.setStatus(0);//未激活:0
        user.setActivation_code(CommunityUtil.generateUUID());//激活码
        //随机头像
        user.setHeader_url(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreate_time(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活路径
        // http://localhost:8081/activation/用户id/激活码
        String url = domain + "/activation/" + user.getId() + "/" + user.getActivation_code();

        context.setVariable("url",url);
        //thymeleaf模板数据导入
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);


        return map;
    }

    //激活码
    public int activation(int user_id, String code){
        User user = userMapper.selectById(user_id);
        if(user.getStatus() != 0){
            return ACTIVATION_REPEAT;
        }else if(user.getActivation_code().equals(code)){
            userMapper.updateUser(user_id,1);
            clearCache(user_id);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    //登录
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }

        //验证密码
        if (!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))) {
            map.put("passwordMsg", "密码错误");
            return map;
        }

        //验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUser_id(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        //把登录凭证存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getKaptchaKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    //查询凭证
    public LoginTicket findLoginTicket(String ticket){
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getKaptchaKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //更新用户头像
    public void updateHeaderUrl(int user_id, String header_url){
        userMapper.updateHeader(user_id, header_url);
        clearCache(user_id);
    }

    //修改密码
    public boolean updatePassword(int user_id, String password, String newPassword){
        User user = userMapper.selectById(user_id);
        if(!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))){
            return false;
        }
        userMapper.updatePassword(user_id, CommunityUtil.md5(newPassword + user.getSalt()));
        clearCache(user_id);
        return true;
    }

    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user,3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清楚缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
