package top.chriszwz.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chriszwz.community.dao.DiscussPostMapper;
import top.chriszwz.community.dao.LoginTicketMapper;
import top.chriszwz.community.dao.UserMapper;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.LoginTicket;
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
    @Test
    void contextLoads() {
        String s = CommunityUtil.generateUUID();
        System.out.println(s);
        System.out.println(s.length());
    }

}
