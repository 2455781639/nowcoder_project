package top.chriszwz.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import top.chriszwz.community.dao.DiscussPostMapper;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.util.SensitiveFilter;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int user_id, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(user_id,offset,limit);
    }

    public int findDiscussPostRows(int user_id){
        return discussPostMapper.selectDiscussPostRows(user_id);
    }

    //新增帖子
    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转义html
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    //查询帖子详情
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }


}
