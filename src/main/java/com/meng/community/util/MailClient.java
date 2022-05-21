package com.meng.community.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Description: 邮箱客户端
 * Created by MenG on 2022/5/21 11:22
 */

@Slf4j(topic = "MailClient.class")
@Component
public class MailClient {


    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     *
     * @param to 接收方
     * @param subject 邮件标题
     * @param content 邮件内同
     */
    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage Message = mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(Message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送邮件失败: "+e.getMessage());
        }

    }


}
