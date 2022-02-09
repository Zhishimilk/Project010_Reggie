package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.SetmealService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    private final SetmealService setmealService;

    public SetmealController(SetmealService setmealService) {
        this.setmealService = setmealService;
    }

    /**
     * 新增 套餐数据
     * @param setmealDto 封装新增套餐的数据对象
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        //使用service层调用方法添加数据
        boolean result = setmealService.add(setmealDto);
        if(result){     //判断service层执行结果
            return R.success("套餐添加成功!");
        }
        return R.error("套餐添加失败!");
    }

    /**
     * 套餐分页条件查询
     * @param page 当前页码
     * @param pageSize 每页显示数据量
     * @param name 出入的查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Long page, Long pageSize, String name){
        //调用service层分页查询
        Page _page = setmealService.pageInfo(page, pageSize, name);
        //响应页面分页条件查询结果
        return R.success(_page);
    }

    /**
     * 根据id查询套餐基本数据和套餐菜品数据
     * @param id 套餐的id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealById(@PathVariable("id") Long id){
        //调用service根据id查找数据
        SetmealDto setmealDto = setmealService.queryById(id);
        //根据查询结果响应页码成功或失败信息
        if(setmealDto != null){
            return R.success(setmealDto);
        }
        return R.error("查询失败!");
    }

    /**
     * 修改单个套餐数据
     * @param setmealDto 分装完整套餐数据的数据传输对象
     * @return
     */
    @PutMapping
    public R<String> modifySingleSetmeal(@RequestBody SetmealDto setmealDto){
        //调用service修改套餐数据
        boolean result = setmealService.modifySingleSetmeal(setmealDto);
        //根据执行结果响应页面对应信息
        if(result){
            return R.success("修改成功!");
        }
        return R.error("修改失败!");
    }

    /**
     * 批量修改套餐售卖状态
     * @param status 最终修改状态
     * @param ids 需要修改状态的多个套餐id
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status, @RequestParam("ids") List<Long> ids){
        //调用service层修改套餐
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("status", status);        //设置修改条件
        boolean result = setmealService.update(updateWrapper);      //修改数据
        //判断修改结果响应对应信息
        if(result){
            return R.success("修改套餐状态成功!");
        }
        return R.error("修改套餐状态失败!");
    }

    /**
     * 批量删除多个套餐
     * @param ids 被删除的套餐id
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        //调用service层删除套餐
        Integer count = setmealService.deleteSetmeal(ids);
        //根据执行结果响应页面消息
        if(count == ids.size()){
            return R.success("删除成功!");
        }else if(count > 0){
            return R.error("已删除部分停售的数据!");
        }
        return R.error("删除失败!请先将要删除的套餐停售!");
    }

    /**
     * 查询菜品分类对应的套餐
     * @param setmeal 封装了查询条件的对象
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        //调用service层查询套餐完整数据
        List<SetmealDto> setmealDtoList = setmealService.SetmealByCategory(setmeal);
        //根据结果响应页面相应信息
        if(setmealDtoList != null){
            return R.success(setmealDtoList);
        }
        return R.success(null);
    }
}
