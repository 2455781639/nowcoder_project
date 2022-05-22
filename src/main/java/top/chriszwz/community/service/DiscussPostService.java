package top.chriszwz.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chriszwz.community.dao.DiscussPostMapper;
import top.chriszwz.community.entity.DiscussPost;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int user_id, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(user_id,offset,limit);
    }

    public int findDiscussPostRows(int user_id){
        return discussPostMapper.selectDiscussPostRows(user_id);
    }

}
