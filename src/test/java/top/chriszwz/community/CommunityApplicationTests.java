package top.chriszwz.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chriszwz.community.dao.DiscussPostMapper;
import top.chriszwz.community.dao.UserMapper;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.entity.User;
import top.chriszwz.community.util.MailClient;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void contextLoads() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(101, 0, 10);
        list.forEach(System.out::println);
    }

    @Autowired
    private MailClient mailClient;

    @Test
    public void mailTest(){
        mailClient.sendMail("zwz2455781639@gmail.com","test","你好这里是Chris发送方测试邮件服务器");
    }

}
