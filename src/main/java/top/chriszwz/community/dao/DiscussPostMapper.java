package top.chriszwz.community.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.chriszwz.community.entity.DiscussPost;

import java.util.List;

/* 
 * @Description: DiscussPost select
 * @Author: Chris(张文卓)
 * @Date: 2022/6/24 9:21
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int user_id,int offset, int limit);

    int selectDiscussPostRows(@Param("user_id") int user_id);
}
