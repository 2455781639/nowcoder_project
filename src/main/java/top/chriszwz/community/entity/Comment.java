package top.chriszwz.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * @Description: 评论
 * @Author: Chris(张文卓)
 * @Date: 2022/6/29 15:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private int user_id;
    private int entity_type;
    private int entity_id;
    private int target_id;
    private String content;
    private int status;
    private Date create_time;
}
