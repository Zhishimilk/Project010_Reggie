package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private DishFlavorService dishFlavorService;
    private CategoryService categoryService;

    @Autowired
    public void setDishFlavorService(DishFlavorService dishFlavorService) {
        this.dishFlavorService = dishFlavorService;
    }

    @Autowired
    @Lazy
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 添加菜品
     * @param dishDto 新增数据封装类
     * @return
     */
    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        //保存基本菜品信息
        boolean result = super.save(dishDto);

        Long dishId = dishDto.getId();      //菜品id

        //设置口味对应的菜品
        List<DishFlavor> dishFlavorList =  dishDto.getFlavors();
        //使用stream流处理菜品口味(dishFlavorList)数据
        dishFlavorList= dishFlavorList.stream()
                .peek((flavors) -> flavors.setDishId(dishId))       //设置口味对应的菜品id属性
                .collect(Collectors.toCollection(ArrayList::new));      //重新封装为List集合
        //保存口味信息
        boolean result2 = dishFlavorService.saveBatch(dishFlavorList);

        return result2 && result;       //返回新增操作执行结果
    }

    /**
     * 菜品分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page page(Long page, Long pageSize, String name) {

        //--查询基础分页数据--
        //分页数据封装类
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //设置条件参数
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), "name", name);
        queryWrapper.orderByDesc("update_time");
        //调用mybatis plus原始方法查询数据
        super.page(dishPage, queryWrapper);

        //--查询菜品所有分类--
        List<Category> categoryList = categoryService.list();
        //将id和分类名称封装到map集合
        HashMap<Long, String> categoryMap = new HashMap<>();
        for (Category category : categoryList) {
            categoryMap.put(category.getId(), category.getName());
        }

        //--将Dish转化为DishDto并封装类型名属性赋值--
        List<Dish> dishList = dishPage.getRecords();
        ArrayList<DishDto> dishDtoList = dishList.stream().map(dish -> {     //stream流的map映射将数据由"key"转化"value"
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);    //克隆基本属性值
            String categoryName = categoryMap.get(dish.getCategoryId());    //根据菜品分类id在分类map中获取分类名称
            dishDto.setCategoryName(categoryName);      //为DishDto分类名属性赋值
            return dishDto;
        }).collect(Collectors.toCollection(ArrayList::new));

        //--将菜品分类和菜品数据分装在新的page类--
        //克隆 dishPage 基本属性值
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");     //忽略 records属性 克隆其他属性
        //将转化后的 dishDtoList 数据分装到dishPage
        dishDtoPage.setRecords(dishDtoList);

        return dishDtoPage;
    }

    /**
     * 通过id查询要修改的数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品(Dish)数据
        Dish dish = super.getById(id);

        //--查询菜品口味(DishFlavor)数据--
        //设置查询参数
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id", id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);

        //将菜品数据(dish)和菜品口味数据(dishFlavor)封装到DishDto返回
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);        //spring对像工具类 copyProperties()克隆对象属性列表
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    /**
     * 修改菜品数据包含口味修改
     * @param dishDto 封装了菜品数据和菜品口味数据
     * @return
     */
    @Override
    @Transactional
    public boolean update(DishDto dishDto) {

        //修改菜品(Dish)数据
        boolean result = super.updateById(dishDto);
        boolean result2 = true;


        //--修改菜品口味(DishFlavor)数据--
        //根据菜品id删除菜品下原有的菜品口味(dishFlavor)
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id", dishDto.getId());    //设置条件
        dishFlavorService.remove(queryWrapper);
        if(dishDto.getFlavors() != null) {      //判断是否有要修改的菜品口味(dishFlavor)
            //获取当前菜品口味(dishFlavor)数据
            List<DishFlavor> dishFlavors = dishDto.getFlavors();
            //菜品口味对应的菜品(dish)id
            Long dishId = dishDto.getId();
            //使用stream流处理菜品口味(dishFlavors)数据
            dishFlavors = dishFlavors.stream()
                    .peek(dishFlavor -> dishFlavor.setDishId(dishId))       //封装菜品(dish)的id
                    .collect(Collectors.toCollection(ArrayList::new));      //重新封装为List集合
            //调用service层将修改后的菜品口味(dishFlavor)保存
            result2 = dishFlavorService.saveBatch(dishFlavors);
        }

        return result && result2;
    }

    /**
     * 批量修改菜品状态
     * @param status 最终修改状态
     * @param ids 被选择要修改状态的多个菜品id
     * @return
     */
    @Override
    public boolean changeStatus(Integer status, List<Long> ids) {
        //调用service方法查找要修改状态的菜品
        List<Dish> dishList = super.listByIds(ids);

        //使用stream流处理菜品(dishList)数据
        dishList = dishList.stream()
                .filter(dish -> !Objects.equals(dish.getStatus(), status))      //过滤不需要修改的菜品
                .peek(dish -> dish.setStatus(status))       //修改菜品状态为参数status
                .collect(Collectors.toCollection(ArrayList::new));      //重新封装为List集合

        //调用service方法保存修改
        boolean result = super.updateBatchById(dishList);

        return result;
    }

    /**
     * 批量删除菜品数据
     * @param ids 用户传入的多个菜品id
     * @return
     */
    @Override
    public boolean remove(List<Long> ids) {

        //查询出用户传入的id下对应的菜品
        List<Dish> dishes = super.listByIds(ids);
        //使用stream流操作数据
        List<Long> _ids = dishes.stream()
                .filter(dish -> dish.getStatus() != 1)      //过滤出已经启售的菜品
                .map(Dish::getId)       //取出过滤后菜品的id
                .collect(Collectors.toCollection(ArrayList::new));      //将过滤的菜品id重新封装为集合
        //判断过滤后是否还有真正需要删除的菜品
        boolean result = false;     //删除的结果默认为false
        if(_ids.size() > 0) {
            //先根据已经过滤后 _ids 删除停售的菜品关联的口味数据
            QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("dish_id", _ids);       //设置条件为包含菜品 _ids 的菜品口味
            dishFlavorService.remove(queryWrapper);     //传入条件删除菜品口味
            //根据已经过滤后 _ids 删除停售的菜品
            result = super.removeByIds(_ids);
        }

        //判断用户传入的id下菜品是否存在启用的菜品
        return ids.size() == _ids.size() && result;
    }

    /**
     * 查询多个菜品数据
     * @param dish 封装了若干查询条件
     * @return
     */
    @Override
    public List<DishDto> dishList(Dish dish) {
        //创建查询封装对象设置条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus());
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //调用service层查询菜品基本数据
        List<Dish> dishes = super.list(queryWrapper);
        //根据查询的结果判断是否继续查询菜品口味的数据
        if(dishes.size()>0){
            //使用stream重新封装所有完整菜品数据
            List<DishDto> dishDtoList = dishes.stream()
                    .map(item -> {       //①将菜品基本数据封装到新的对象
                        DishDto dishDto = new DishDto();
                        BeanUtils.copyProperties(item, dishDto);
                        return dishDto;
                    })
                    .peek(item -> {       //②将菜品和菜品口味分装到新的对象
                        //调用service根据菜品id查询对应的口味信息
                        LambdaQueryWrapper<DishFlavor> _queryWrapper = new LambdaQueryWrapper<>();
                        _queryWrapper.eq(DishFlavor::getDishId, item.getId());
                        List<DishFlavor> dishFlavors = dishFlavorService.list(_queryWrapper);
                        //封装口味信息到flavors属性
                        item.setFlavors(dishFlavors);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
            return dishDtoList;
        }
        return null;
    }
}
