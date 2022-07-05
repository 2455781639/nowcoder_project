package top.chriszwz.community.dao;

import org.apache.ibatis.annotations.Mapper;
import top.chriszwz.community.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    //根据实体类型和实体id查询评论列表
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询评论数量
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
