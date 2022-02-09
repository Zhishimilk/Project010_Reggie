package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 验证码发送
     * @param user 用户信息封装对象 包含收取验证码的手机号码
     * @param session 存储验证码方便后续使用
     * @return
     */
    @PostMapping("/sendMsg")
    public R<Integer> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号码
        String phone = user.getPhone();
        //非空校验
        if(StringUtils.isNotEmpty(phone)){
            //生成随机验证码
            Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
            log.info(validateCode.toString());
            //编辑短信并将验证码发送给用户
            //... ...
            //将验证码存入session会话以便后续使用
            session.setAttribute(phone, validateCode);

            return R.success(validateCode);
        }
        return R.error("验证码发送失败请重试!");
    }

    /**
     * 登录校验
     * @param map 用户登录数据 (手机号码, 验证码)
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session){
        //从map中取出用户号码和验证码
        String phone = (String) map.get("phone");
        String inCode = map.get("code").toString();
        //从session会话中取出验证码进行校验
        String validateCode = session.getAttribute(phone).toString();
        if(validateCode != null && validateCode.equals(inCode)){
            //校验成功!允许登录
            //--为用户自动注册
            userService.register(phone, session);
            return R.success("登录成功!");
        }
        return R.error("登录失败!验证码错误!");
    }
}
