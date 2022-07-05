package top.chriszwz.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import top.chriszwz.community.entity.LoginTicket;

/* 
 * @Description: 登录数据访问层
 * @Author: Chris(张文卓)
 * @Date: 2022/6/24 9:42
 */
@Mapper
@Deprecated//声明组件不推荐使用
public interface LoginTicketMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket, int status);
}
