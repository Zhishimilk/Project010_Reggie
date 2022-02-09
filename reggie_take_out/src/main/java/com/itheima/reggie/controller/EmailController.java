package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 发送邮件验证码
     * @param emailReceiver 收件人
     * @return
     */
    @GetMapping("/getCode")
    public R<String> sendVerificationCode(String emailReceiver){
        //调用service层发送验证码
        boolean result = emailService.sendVerificationCode(emailReceiver);
        if(result){
            return R.success("已发送验证码!");
        }
        return R.error("验证码发送失败!");
    }
}
