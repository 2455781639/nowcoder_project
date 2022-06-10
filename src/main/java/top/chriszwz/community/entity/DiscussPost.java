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
    private int id;//帖子id
    private int user_id;//帖子作者id
    private String title;//帖子标题
    private String content;//帖子内容
    private int type;//0-普通; 1-置顶;
    private int status;//0-正常; 1-精华; 2-拉黑;
    private Date create_time;//帖子创建时间
    private int comment_count;//评论数量
    private Double score;//得分
}
