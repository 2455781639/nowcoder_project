package top.chriszwz.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/* 
 * @Description: 用户
 * @Author: Chris(张文卓)
 * @Date: 2022/5/21 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;//用户id
    private String username;//用户名
    private String password;//密码
    private String salt;//加密盐
    private String email;//邮箱
    private int type;//0-普通用户; 1-超级管理员; 2-版主;
    private int status;//0-未激活; 1-已激活;
    private String activation_code;//激活码
    private String header_url;//头像url
    private Date create_time;//用户创建时间
}
