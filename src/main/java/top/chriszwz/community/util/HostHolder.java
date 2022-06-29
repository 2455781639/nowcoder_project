package top.chriszwz.community.util;

import org.springframework.stereotype.Component;
import top.chriszwz.community.entity.User;

/*
 * @Description: 持有用户信息，用于代替Session对象
 * @Author: Chris(张文卓)
 * @Date: 2022/6/25 9:23
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
