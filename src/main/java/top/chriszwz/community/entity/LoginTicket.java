package top.chriszwz.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket {
    private int id;
    private int user_id;
    private String ticket;//登陆凭证
    private int status;//0-登录，1-退出
    private Date expired;//过期时间
}
