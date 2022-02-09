package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 登录请求
     * @param request
     * @param employee_ 登录的员工信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee_){
        // 1. 根据用户名查询对应的用户信息
        // 创建Wrapper类设置查询条件
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq(employee_.getUsername() != null, "username", employee_.getUsername());
        // 调用service层的方法传入条件查询数据
        Employee employee = employeeService.getOne(wrapper);

        // R类封装查询结果
        R<Employee> data;

        // 2. 判断登录信息是否满足查询结果
        if(employee != null){       // 判断用户是否存在
            // 将用户输入密码进行加密获取MD5加密码
            String password = DigestUtils.md5DigestAsHex(employee_.getPassword().getBytes(StandardCharsets.UTF_8));
            if(employee.getStatus() == 0){      // 判断当前用户是否禁用
                data = R.error("当前用户已被禁用!");
            }else if(!employee.getPassword().equals(password)){     // 判断当前用户密码是否匹配
                data = R.error("用户密码错误!");
            }else {
                // 满足所有条件返回登录用户数据
                data = R.success(employee);
            }
        }else {
            data = R.error("用户名不存在!");
        }

        // 将当前用户id存入Session域中在多页面中访问
        request.getSession().setAttribute("employee", employee.getId());
        ObjectMapper mapper = new ObjectMapper();

        return data; //响应页面登录结果
    }

    /**
     * 退出请求
     * @param request 当前已登录账号
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session域中保存的用户信息
        request.getSession().invalidate();
        return R.success("退出登录成功!");    //响应成功消息
    }

    /**
     * 新增请求
     * @param request 获取执行添加操作的用户
     * @param employee_ 新添加的员工数据
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee_){

        //------------------------准备需要存储的信息-------------------------->
        //CREATE TABLE `reggie`.`employee`  (
        //  `id` bigint NOT NULL COMMENT '主键',      √
        //  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '姓名',       √
        //  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户名',      √
        //  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
        employee_.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));       //初始员工密码为 123456
        //  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',     √
        //  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '性别',     √
        //  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '身份证号',        √
        //  `status` int NOT NULL DEFAULT 1 COMMENT '状态 0:禁用，1:正常',     √
        //  `create_time` datetime NOT NULL COMMENT '创建时间',     √
        //  `update_time` datetime NOT NULL COMMENT '更新时间',     √
        //  `create_user` bigint NOT NULL COMMENT '创建人',     √
        //  `update_user` bigint NOT NULL COMMENT '修改人',     √
        //  PRIMARY KEY (`id`) USING BTREE,
        //  UNIQUE INDEX `idx_username`(`username`) USING BTREE
        //) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '员工信息' ROW_FORMAT = Dynamic;

        //调用service层方法添加新数据
        boolean result = employeeService.save(employee_);

        //判断添加结果响应消息
        if(result){
            return R.success("添加成功!");
        }
        return R.error("添加失败!");
    }

    /**
     * 分页条件查询
     * @param page 当前页
     * @param pageSize 每页显示数量
     * @param name 查询条件
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page> page(Long page, Long pageSize, String name){

        //设置分页参数
        Page<Employee> pagination = new Page<>(page, pageSize);

        //设置查询条件
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), "name", name);
        queryWrapper.orderByDesc("update_time");

        //调用service层执行分页条件查询
        employeeService.page(pagination, queryWrapper);

        return R.success(pagination);   //返回分页查询结果
    }

    /**
     * 修改数据请求
     * @param request
     * @param employee_
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee_){

        //------------------------准备需要存储的信息-------------------------->
        //CREATE TABLE `reggie`.`employee`  (
        //  `id` bigint NOT NULL COMMENT '主键',      √
        //  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '姓名',       √
        //  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户名',      √
        //  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',       √
        //  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',     √
        //  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '性别',     √
        //  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '身份证号',        √
        //  `status` int NOT NULL DEFAULT 1 COMMENT '状态 0:禁用，1:正常',     √
        //  `create_time` datetime NOT NULL COMMENT '创建时间',     √
        //  `update_time` datetime NOT NULL COMMENT '更新时间',     √
        //  `create_user` bigint NOT NULL COMMENT '创建人',        √
        //  `update_user` bigint NOT NULL COMMENT '修改人',        √
        //  PRIMARY KEY (`id`) USING BTREE,
        //  UNIQUE INDEX `idx_username`(`username`) USING BTREE
        //) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '员工信息' ROW_FORMAT = Dynamic;

        //设置修改条件
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", employee_.getId());

        //调用service层方法传入条件修改数据
        boolean result = employeeService.update(employee_, queryWrapper);

        //判断结果响应数据
        if(result){
            return R.success("修改成功!");
        }
        return R.error("修改失败!");
    }

    /**
     * 查询id请求
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){

        //调用service层执行id查询
        Employee employee = employeeService.getById(id);

        //根据结果响应信息
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有找到要修改的对象!");
    }

    /**
     * 测试方法
     * @return
     */
    @RequestMapping("/test")    //测试访问
    public String test(HttpServletResponse response){
        response.setContentType("image/png");
        System.out.println("ok!");
        return "ok!";
    }

}
