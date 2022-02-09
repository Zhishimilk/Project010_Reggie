package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐方法
    boolean add(SetmealDto setmealDto);

    //分页条件查询
    Page pageInfo(Long page, Long pageSize, String name);

    //根据id查询
    SetmealDto queryById(Long id);

    //修改单个套餐数据
    boolean modifySingleSetmeal(SetmealDto setmealDto);

    //批量删除套餐数据
    Integer deleteSetmeal(List<Long> ids);

    //根据菜品分类查询套餐
    List<SetmealDto> SetmealByCategory(Setmeal setmeal);
}
