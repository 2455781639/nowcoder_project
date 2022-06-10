package top.chriszwz.community.util;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;//邮件发送方

    public void sendMail(String to,String subject, String content){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            //使用帮助类helper
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            //设置发送方
            helper.setFrom(from);
            //设置接收方
            helper.setTo(to);
            //设置邮件主题
            helper.setSubject(subject);
            //设置邮件内容，支持html
            helper.setText(content, true);
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("邮件发送失败："+e.getMessage());
        }
    }


}
