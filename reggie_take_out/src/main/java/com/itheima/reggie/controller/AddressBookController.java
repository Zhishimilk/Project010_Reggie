package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    private AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    /**
     * 新增收货地址
     * @param addressBook 封装用户收获地址数据对象
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        //封装登录用户id数据到属性 addressBook.userId
        addressBook.setUserId(BaseContext.getCurrentId());
        //调用service层保存收获地址
        boolean result = addressBookService.save(addressBook);
        if(result){
            return R.success("添加地址成!");
        }
        return R.error("地址添加失败!");
    }

    /**
     * 根据登录用户id查询所有收获地址
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //从线程局部变量中获取登录用户id
        Long userId = BaseContext.getCurrentId();
        //设置查询条件
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        //调用service层查询地址数据
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        //响应页面查询结果
        return R.success(addressBooks);
    }

    /**
     * 设置默认地址
     * @param addressBook 封装用户收获地址数据对象
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook){
        //调用service层修改默认地址
        boolean result = addressBookService.updateDefaultAddress(addressBook);
        if(result){     //判断修改默认地址结果
            return R.success("修改成功!");
        }
        return R.error("修改失败!");
    }

    /**
     * 查询单个地址
     * @param id 被查询地址的id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> addressFindOne(@PathVariable("id") Long id){
        //调用service层查找要修改的地址
        AddressBook addressBook = addressBookService.getById(id);
        //判断查询结果响应相关数据
        if(addressBook != null){
            return R.success(addressBook);
        }
        return R.error("找不到要修改的数据");
    }

    /**
     * 修改地址
     * @param addressBook 封装用户收获地址数据对象
     * @return
     */
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook){
        //调用service层修改保存地址
        boolean result = addressBookService.updateById(addressBook);
        //判断结果响应相关数据
        if(result){
            return R.success("地址修改成功!");
        }
        return R.error("修改地址失败!");
    }

    /**
     * 删除用户收获地址
     * @param addressBook 封装用户收获地址数据对象
     * @return
     */
    @DeleteMapping
    public R<String> deleteAddress(@RequestParam("ids") List<Long> ids){
        //调用service层方法删除用户地址
        boolean result = addressBookService.removeByIds(ids);
        //判断结果响应相关数据
        if(result){
            return R.success("删除收获地址成功!");
        }
        return R.error("删除收获地址失败!");
    }
}
