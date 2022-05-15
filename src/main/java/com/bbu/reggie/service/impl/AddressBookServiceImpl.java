package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.entity.AddressBook;
import com.bbu.reggie.service.AddressBookService;
import com.bbu.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author 黄东
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-05-13 10:05:39
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




