package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    // 保存菜品和菜品口味
    boolean saveWithFlavor(DishDto dishDto);

    //菜品分页条件查询
    Page page(Long page, Long pageSize, String name);

    //查询单个菜品数据
    DishDto getByIdWithFlavor(Long id);

    //修改菜品数据
    boolean update(DishDto dishDto);

    //批量修改菜品状态
    boolean changeStatus(Integer status, List<Long> ids);

    //批量删除菜品数据
    boolean remove(List<Long> ids);

    //查询多个菜品数据
    List<DishDto> dishList(Dish dish);
}
