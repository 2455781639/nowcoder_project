package top.chriszwz.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/* 
 * @Description: 帖子
 * @Author: Chris(张文卓)
 * @Date: 2022/5/21 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscussPost {
    private int id;
    private int user_id;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date create_time;
    private int comment_count;
    private Double score;
}
