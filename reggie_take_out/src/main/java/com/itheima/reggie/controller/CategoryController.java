package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 新增菜品(/套餐)分类
     * @param category_
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category_) {

        //------------------------准备需要存储的信息-------------------------->
        //  CREATE TABLE `reggie`.`category`(
        //  `id`bigint NOT NULL COMMENT '主键',       √
        //  `type`int NULL DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',       √
        //  `name`varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '分类名称',       √
        //  `sort`int NOT NULL DEFAULT 0 COMMENT '顺序',       √
        //  `create_time`datetime NOT NULL COMMENT '创建时间',       √
        //  `update_time`datetime NOT NULL COMMENT '更新时间',       √
        //  `create_user`bigint NOT NULL COMMENT '创建人',       √
        //  `update_user`bigint NOT NULL COMMENT '修改人',       √
        //  PRIMARY KEY(`id`) USING BTREE,
        //  UNIQUE INDEX `idx_category_name`(`name`)USING BTREE
        //  )ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品及套餐分类' ROW_FORMAT = Dynamic;

        //调用service层方法执行新增将数据添加到数据库
        boolean result = categoryService.save(category_);

        //判断执行结果响应页面信息
        if(result){
            return R.success("添加成功!");
        }
        return R.error("添加失败!");
    }

    /**
     * (分类)分页查询
     * @param page 当前页
     * @param pageSize 每页显示数据量
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Long page, Long pageSize){
        //分页设置
        Page<Category> pagination = new Page<>(page, pageSize);
        //设置查询条件
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        //调用service层执行分页查询
        categoryService.page(pagination, queryWrapper);
        //将查询结果响应页面
        return R.success(pagination);
    }

    /**
     * 删除单个数据
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        //调用service层方法执行删除单个数据
        boolean result = categoryService.remove(id);
        //判断结果响应页面消息
        if(result){
            return R.success("删除成功!");
        }
        return R.error("删除失败!");
    }

    /**
     * 修改分类属性
     * @param _category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category _category){

        //调用service层执行方法修改数据
        boolean result = categoryService.updateById(_category);
        if(result){     //判断结果响应页面消息
            return R.success("修改分类成功!");
        }
        return R.error("修改分类失败!");
    }

    /**
     * 查询菜品分类
     * @param category 查询条件
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Integer type){

        //设置查询条件
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(type != null, "type", type);
        queryWrapper.orderByAsc("sort");
        //调用service方法查询数据
        List<Category> categoryList = categoryService.list(queryWrapper);
        if(categoryList != null){
            return R.success(categoryList);
        }
        return R.error("菜品分类查询失败");
    }
}
