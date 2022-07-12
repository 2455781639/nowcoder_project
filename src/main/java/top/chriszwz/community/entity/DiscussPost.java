package top.chriszwz.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/* 
 * @Description: 帖子
 * @Author: Chris(张文卓)
 * @Date: 2022/5/21 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "discusspost", type = "_doc", shards = 6, replicas = 3)//shards:分片数量，replicas:副本数量
public class DiscussPost {

    @Id
    private int id;//帖子id

    @Field(type = FieldType.Integer)
    private int user_id;//帖子作者id

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")//analyzer:分词器，searchAnalyzer:搜索分词器
    private String title;//帖子标题

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;//帖子内容

    @Field(type = FieldType.Integer)
    private int type;//0-普通; 1-置顶;

    @Field(type = FieldType.Integer)
    private int status;//0-正常; 1-精华; 2-拉黑;

    @Field(type = FieldType.Date)
    private Date create_time;//帖子创建时间

    @Field(type = FieldType.Integer)
    private int comment_count;//评论数量

    @Field(type = FieldType.Double)
    private Double score;//得分
}
