package top.chriszwz.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chriszwz.community.dao.UserMapper;
import top.chriszwz.community.entity.User;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findById(int id){
        return userMapper.selectById(id);
    }


}
