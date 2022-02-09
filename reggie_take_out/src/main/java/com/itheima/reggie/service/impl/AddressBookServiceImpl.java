package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    /**
     * 修改当前默认地址
     * @param addressBook 最终默认地址
     * @return
     */
    @Override
    public boolean updateDefaultAddress(AddressBook addressBook) {
        //获取当前登录用户id
        Long userId = BaseContext.getCurrentId();
        //修改取消旧的默认地址
        UpdateWrapper<AddressBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_default", 0);     //设置默认地址状态为0
        updateWrapper.eq("user_id", userId);    //设置修改条件
        super.update(updateWrapper);    //将默认地址取消

        //将当前地址修改为默认地址
        addressBook.setIsDefault(1);     //设置默认地址状态为1
        boolean result = super.updateById(addressBook);   //修改传入的地址为默认地址
        return result;
    }
}
