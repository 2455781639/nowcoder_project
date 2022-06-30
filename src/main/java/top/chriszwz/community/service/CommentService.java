package top.chriszwz.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import top.chriszwz.community.dao.CommentMapper;
import top.chriszwz.community.entity.Comment;
import top.chriszwz.community.util.CommunityConstant;
import top.chriszwz.community.util.SensitiveFilter;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentCountByEntity(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    //增加评论
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)//事务注解
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义html
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新评论数量
        if(comment.getEntity_type() == ENTITY_TYPE_POST){
            //评论帖子
            int count = commentMapper.selectCountByEntity(comment.getEntity_type(), comment.getEntity_id());
            discussPostService.updateCommentCount(comment.getEntity_id(),count);
        }else if(comment.getEntity_type() == ENTITY_TYPE_COMMENT){

        }
        return rows;
    }
}
