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
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activation_code;
    private String header_url;
    private Date create_time;
}
