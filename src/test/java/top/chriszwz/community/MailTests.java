package top.chriszwz.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.chriszwz.community.util.MailClient;

@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void mailTest(){
        mailClient.sendMail("zwz2455781639@gmail.com","test","你好这里是Chris发送方测试邮件服务器");
    }

    @Test
    public void testHtml(){
        Context context = new Context();
        context.setVariable("username","chris");

        //thymeleaf模板数据导入
        String process = templateEngine.process("mail/demo", context);
        System.out.println(process);

        mailClient.sendMail("zwz2455781639@gmail.com","HtmlTest",process);
    }
}
