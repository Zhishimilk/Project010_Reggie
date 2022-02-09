package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 自动注册不设置密码
     * @param phone 用户手机号码
     * @param session
     */
    @Override
    public void register(String phone, HttpSession session) {

        //查询用户是否已经注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);        //设置查询条件
        User _user = super.getOne(queryWrapper);        //查询用户
        if(_user == null) {
            //创建User对象封装用户数据
            _user = new User();
            _user.setStatus(1);
            _user.setPhone(phone);       //设置用户电话属性

            //调用service执行添加用户操作
            super.save(_user);
        }
        //将用户id存入session会话完成登录
        session.setAttribute("user", _user.getId());
    }
}
