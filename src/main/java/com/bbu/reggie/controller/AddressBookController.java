package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bbu.reggie.common.BaseContext;
import com.bbu.reggie.common.R;
import com.bbu.reggie.entity.AddressBook;
import com.bbu.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 保存用户的收获地址信息
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> saveAddress(@RequestBody AddressBook addressBook){
        //设置用户的id
        addressBook.setUserId(BaseContext.getCurrentId());
        //保存信息
        addressBookService.save(addressBook);
        return R.success("成功");
    }

    /**
     * 查询当前用户的地址信息
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getAddressBook(){
        //获取用户的id
        Long userId = BaseContext.getCurrentId();
        //根据id查询地址信息
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId)
                .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return R.success(addressBooks);
    }


    /**
     * 修改地址为默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        //查询属性isDefault是1的数据，将其设置为0
        LambdaUpdateWrapper<AddressBook> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.eq(AddressBook::getUserId,BaseContext.getCurrentId())
                .eq(AddressBook::getIsDefault,1)
                .set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper1);
        //将改地址信息的isDefault设置为1
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AddressBook::getIsDefault,1)
                .eq(AddressBook::getId,addressBook.getId());
        addressBookService.update(updateWrapper);
        return R.success("成功");
    }

    /**
     * 根据id查询地址的详细信息
     * @param addressId
     * @return
     */
    @GetMapping("/{addressId}")
    public R<AddressBook> getAddressDetail(@PathVariable Long addressId){
        //根据id查询
        AddressBook addressBook = addressBookService.getById(addressId);
        return R.success(addressBook);
    }

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> editAddressBook(@RequestBody AddressBook addressBook){
//        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
//        updateWrapper.eq(AddressBook::getId,addressBook.getId());
//        addressBookService.update(addressBook,updateWrapper);
        addressBookService.updateById(addressBook);
        return R.success("成功");
    }

    /**
     * 查询默认的地址信息
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> defaultAddress(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        //查询本用户的，且isDefault为1的地址
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(AddressBook::getUserId,userId)
                .eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }
}

