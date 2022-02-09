package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    private final DishService dishService;
    private final SetmealService setmealService;

    public CategoryServiceImpl(DishService dishService, SetmealService setmealService) {
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    /**
     * 根据id删除对应的数据
     * @param id
     */
    @Override
    public boolean remove(Long id) {

        //查询id分类下是否有菜品数据↓
        //设置查询条件
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", id);
        //执行查询 根据结果是否抛出异常来取消删除操作
        int count = dishService.count(queryWrapper);
        if(count > 0){
            //抛出异常取消本次操作
            throw new CustomException("该数据存在子数据不能直接删除!");
        }

        //查询id分类下是否有套餐数据↓
        //设置查询条件
        QueryWrapper<Setmeal> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("category_id", id);
        //执行查询 根据结果是否抛出异常来取消删除操作
        count = setmealService.count(queryWrapper2);
        if(count > 0){
            //抛出异常取消本次操作
            throw new CustomException("该数据存在子数据不能直接删除!");
        }

        //没有异常正常执行删除
        return super.removeById(id);
    }
}
