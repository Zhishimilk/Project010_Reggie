package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    private final SetmealDishService setmealDishService;
    private final CategoryService categoryService;

    @Lazy
    public SetmealServiceImpl(SetmealDishService setmealDishService, CategoryService categoryService) {
        this.setmealDishService = setmealDishService;
        this.categoryService = categoryService;
    }

    /**
     * 新增套餐
     * @param setmealDto 封装套餐数据对象
     * @return
     */
    @Override
    @Transactional
    public boolean add(SetmealDto setmealDto) {

        //①保存套餐基本数据
        boolean result1 = super.save(setmealDto);

        //②保存套餐与菜品关系数据
        Long setmealId = setmealDto.getId();    //套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();    //获取套餐内菜品数据
        for (SetmealDish setmealDish : setmealDishes) {     //设置套餐内菜品的套餐id
            setmealDish.setSetmealId(setmealId);
        }
        boolean result2 = setmealDishService.saveBatch(setmealDishes);      //调用对应service类方法保存数据

        return result2 && result1;
    }

    /**
     * 分页条件查询
     * @param page 用户传入的页码
     * @param pageSize 每页显示的数据量
     * @param name 用户传入的查询条件
     * @return
     */
    @Override
    public Page pageInfo(Long page, Long pageSize, String name) {
        //①处理用户输入的条件
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), "name", name);

        //②执行查询方法获取套餐分页数据
        super.page(pageInfo, queryWrapper);
        List<Setmeal> setmeals = pageInfo.getRecords();

        //③为每个套餐增添套餐分类名称属性
        //-获取所有套餐分类存入map集合(key=id, value=name);
        List<Category> categories = categoryService.list().stream()
                .filter(category -> 2 == category.getType())
                .collect(Collectors.toCollection(ArrayList::new));
        HashMap<Long, String> categoryMap = new HashMap<>();

        for (Category category : categories) {      //将分类id和分类名称键值对存入map集合
            categoryMap.put(category.getId(), category.getName());
        }

        //-套餐分页数据封装到新的List<SetmealDto>中
        List<SetmealDto> setmealDtos = setmeals.stream()
                .map(setmeal -> {
                    SetmealDto setmealDto = new SetmealDto();       //创建SetmealDto对象
                    BeanUtils.copyProperties(setmeal, setmealDto);      //克隆套餐基本数据
                    setmealDto.setCategoryName(categoryMap.get(setmeal.getCategoryId()));       //设置套餐分类名称
                    return setmealDto;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        //⑤重新封装完整分页数据并返回
        Page<SetmealDto> pageInfoPro = new Page<>();
        BeanUtils.copyProperties(pageInfo, pageInfoPro, "records");     //克隆基本分页数据
        pageInfoPro.setRecords(setmealDtos);        //设置完整记录数据 setmealDtos

        return pageInfoPro;
    }

    /**
     * 根据id查询对应套餐的数据以及关联的套餐数据
     * @param id 传入的套餐id
     * @return
     */
    @Override
    public SetmealDto queryById(Long id) {
        //①根据 id 查询套餐基本信息
        Setmeal setmeal = super.getById(id);

        if(setmeal != null) {
            //②根据 id 查询套餐关联的菜品数据
            QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("setmeal_id", id);      //设置查询条件
            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);        //套餐关联的菜品数据

            //③将数据封装为SetmealDto返回
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto, "setmealDishes");     //克隆套餐基本数据
            setmealDto.setSetmealDishes(setmealDishes);     //设置套餐关联的菜品数据

            return setmealDto;
        }

        return null;
    }

    /**
     * 修改单个套餐数据
     * @param setmealDto 封装了套餐完整数据的数据传输类
     * @return
     */
    @Override
    @Transactional
    public boolean modifySingleSetmeal(SetmealDto setmealDto) {
        //①修改套餐基本数据
        boolean result1 = super.updateById(setmealDto);

        //②删除旧的套餐关联的菜品数据
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id", setmealDto.getId());      //设置删除条件
        setmealDishService.remove(queryWrapper);        //删除数据

        //③保存新的套餐关联的菜品数据
        Long setmealId = setmealDto.getId();        //套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream()        //新修改的套餐菜品关联数据 setmealDishes
                .peek(setmealDish -> setmealDish.setSetmealId(setmealId))       //设置套餐id
                .collect(Collectors.toCollection(ArrayList::new));
        boolean result2 = setmealDishService.saveBatch(setmealDishes);      //保存

        return result1 && result2;
    }

    /**
     * 批量删除套餐数据
     * @param ids 被执行删除的套餐id
     * @return
     */
    @Override
    @Transactional
    public Integer deleteSetmeal(List<Long> ids) {
        //①查询套餐是否正在售卖状态
        List<Setmeal> setmeals = super.listByIds(ids);
        List<Long> _ids = setmeals.stream()       //过滤启售状态的套餐
                .filter(setmeal -> setmeal.getStatus() == 0)
                .map(Setmeal::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        if(_ids.size() > 0) {        //判断是否有以及停售的套餐
            //②删除套餐下关联的菜品数据
            QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("setmeal_id", _ids);     //根据套餐id设置删除条件
            setmealDishService.remove(queryWrapper);      //执行删除套餐关联菜品数据

            //③删除套餐数据
            super.removeByIds(_ids);       //执行删除套餐数据
        }

        return _ids.size();      //返回被删除的套餐数量
    }

    /**
     * 根据菜品分类查询对应的套餐数据
     * @param setmeal 封装了查询条件的对象
     * @return
     */
    @Override
    public List<SetmealDto> SetmealByCategory(Setmeal setmeal) {
        //创建条件对象分装相应条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //查询对应菜品分类套餐基本数据
        List<Setmeal> setmealList = super.list(queryWrapper);

        //根据基本数据查询结果判断是否继续查询完整数据
        if(setmealList.size() > 0) {
            //使用stream流对集合setmealList 进行二次封装
            List<SetmealDto> setmealDtoList = setmealList.stream()
                    .map(item -> {       //①将基本数据封装为DTO对象
                        SetmealDto setmealDto = new SetmealDto();   //DTO对象
                        BeanUtils.copyProperties(item, setmealDto);     //克隆原始数据
                        return setmealDto;
                    })
                    .peek(item -> {       //②将DTO对象的其他属性数据补全
                        LambdaQueryWrapper<SetmealDish> _queryWrapper = new LambdaQueryWrapper<>();
                        _queryWrapper.eq(SetmealDish::getSetmealId, item.getId());      //设置查询条件
                        List<SetmealDish> setmealDishes = setmealDishService.list(_queryWrapper);       //根据条件查询套餐对应的菜品数据
                        item.setSetmealDishes(setmealDishes);   //将菜品数据封装到新对象的属性中
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
            return setmealDtoList;
        }
        return null;
    }
}
