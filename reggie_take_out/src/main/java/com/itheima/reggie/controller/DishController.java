package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * 新增菜品
     * @param dishDto 页面数据封装对象
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        //调用service方法添加数据
        boolean result = dishService.saveWithFlavor(dishDto);
        if(result){     //判断结果响应页面消息
            return R.success("新增菜品成功!");
        }
        return R.error("新增菜品失败!");
    }

    /**
     * 菜品分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Long page, Long pageSize, String name){

        //调用service方法查询数据
        Page pagination = dishService.page(page, pageSize, name);
        if(pagination.getRecords() != null){        //判断查询结果
            return R.success(pagination);      //将数据响应回页面
        }

        return R.error("菜品查询失败!");      //返回页面失败消息
    }

    /**
     * 通过id查询要修改的数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable("id") Long id){

        //调用service方法根据id查询数据
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        if(dishDto != null){        //判断查询结果是否成功
            return R.success(dishDto);     //响应页面查询结果
        }

        return R.error("没有找到要修改id的数据");
    }

    /**
     * 修改菜品数据
     * @param dishDto 封装了修改的数据
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        //调用service层修改方法修改数据
        boolean result = dishService.update(dishDto);

        if(result) {     //判断结果是否修改数据成功
            return R.success("菜品修改成功!");
        }
        return R.error("菜品修改失败!");
    }

    /**
     * 修改菜品销售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus( @PathVariable("status") Integer status, @RequestParam("ids") List<Long> ids ){
        log.info(ids.toString());
        //调用service层修改菜品状态(status)
        boolean result = dishService.changeStatus(status, ids);

        if(result){
            return R.success("修改状态成功!");
        }
        return R.error("修改状态失败!");
    }

    /**
     * 批量删除菜品
     * @param ids 将要被删除的菜品id
     * @return
     */
    @DeleteMapping
    public R<String> removeDish(@RequestParam("ids") List<Long> ids){

        //调用service层方法根据用户传入的id删除菜品
        boolean result = dishService.remove(ids);
        if(result){     //判断service层执行结果
            return R.success("删除成功!");
        }
        return R.error("未删除还在启售的菜品!");
    }

    /**
     * 查询菜品数据
     * @param dish 封装页面传入的数据菜品分类(category_id...)
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //调用service层查询菜品数据
        List<DishDto> dishDtoList = dishService.dishList(dish);
        //根据查询结果响应页面相关信息
        if(dishDtoList != null) {
            return R.success(dishDtoList);
        }
        return R.error("菜品查询失败!");
    }
}