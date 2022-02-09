package com.itheima.reggie.service.impl;

import com.itheima.reggie.service.EmailService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public boolean sendVerificationCode(String emailReceiver) {
        //构建一个邮件对象
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //设置邮件发送者
        mailMessage.setFrom(sender);
        //设置邮件接收者
        mailMessage.setTo(emailReceiver);
        //设置文件主题
        mailMessage.setSubject("登录/注册验证码");
        //设置邮箱正文
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);       //生成验证码
        String text = "您的验证码为: "+validateCode+", 请勿轻易透露给他人";
        mailMessage.setText(text);
        try {
            javaMailSender.send(mailMessage);
            return true;
        }catch (MailException e){
            log.info(e.getMessage());
        }
        return false;
    }
}
