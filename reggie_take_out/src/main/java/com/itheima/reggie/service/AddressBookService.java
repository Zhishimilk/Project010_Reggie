package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    //修改默认地址
    boolean updateDefaultAddress(AddressBook addressBook);
}
