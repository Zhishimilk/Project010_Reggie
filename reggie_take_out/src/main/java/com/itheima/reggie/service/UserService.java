package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.User;

import javax.servlet.http.HttpSession;

public interface UserService extends IService<User> {
    //自动注册
    void register(String phone, HttpSession session);
}
