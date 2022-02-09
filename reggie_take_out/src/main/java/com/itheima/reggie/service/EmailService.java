package com.itheima.reggie.service;

public interface EmailService {
    //发送验证码邮件
    boolean sendVerificationCode(String emailReceiver);
}
