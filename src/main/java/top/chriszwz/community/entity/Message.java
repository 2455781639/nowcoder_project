package top.chriszwz.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * @Description: 私信实体类
 * @Author: Chris(张文卓)
 * @Date: 2022/6/30 7:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private int id;
    private int from_id;
    private int to_id;
    private String conversation_id;
    private String content;
    private int status;
    private Date create_time;
}
