package com.bbu.reggie.mapper;

import com.bbu.reggie.entity.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 黄东
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2022-05-13 10:05:39
* @Entity com.bbu.reggie.entity.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




