package top.chriszwz.community.dao;


import org.apache.ibatis.annotations.Mapper;
import top.chriszwz.community.entity.User;

/*
 * @Description: User's table select/delete/update/insert
 * @Author: Chris(张文卓)
 * @Date: 2022/5/21 11:24
 */
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateUser(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);
}
