package top.chriszwz.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chriszwz.community.dao.DiscussPostMapper;
import top.chriszwz.community.dao.LoginTicketMapper;
import top.chriszwz.community.dao.MessageMapper;
import top.chriszwz.community.dao.UserMapper;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.LoginTicket;
import top.chriszwz.community.entity.Message;
import top.chriszwz.community.util.CommunityUtil;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;
    @Test
    void contextLoads() {
        String s = "你好，我是张文卓";
        for(int i = 0; i < s.length(); i++) {
            System.out.println(s.charAt(i));
        }
    }

}
