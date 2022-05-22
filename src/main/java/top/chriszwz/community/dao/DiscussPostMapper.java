package top.chriszwz.community.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.chriszwz.community.entity.DiscussPost;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int user_id,int offset, int limit);

    int selectDiscussPostRows(@Param("user_id") int user_id);
}
