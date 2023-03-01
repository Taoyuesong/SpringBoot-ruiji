package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.dto.DishDto;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 陶月松
 * @create 2023-02-27 21:31
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;


    //增添功能
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info("添加的新菜品为{}",dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    //展示列表功能
    @GetMapping("/page")
    public R<Page<DishDto>> pageList(Integer page,Integer pageSize,String name){
        log.info("本次加载的dish种类，需要构建的page{},pageSize{},name{}",page,pageSize,name);

        //第一步先借助dishService查，原始数据
        Page<Dish> dishPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null,Dish::getName,name);
        lambdaQueryWrapper.orderByAsc(Dish::getUpdateTime);
        dishService.page(dishPage,lambdaQueryWrapper);

        //第二步，构建disDTO,把菜品分类补上
        Page<DishDto> dishDtoPage = new Page<>();
        List<DishDto> dishDtoList = new ArrayList<>();
        //对象拷贝,这里是不dishPage的信息拷贝到dishDtoPage内，但是把其中records的信息给忽略掉了
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        //把其中records的信息单独提取出来进行处理
        List<Dish> dishPageRecords = dishPage.getRecords();

        Iterator<Dish> iterator = dishPageRecords.iterator();
        while (iterator.hasNext()){
            DishDto dishDto = new DishDto();
            Dish next = iterator.next();

            BeanUtils.copyProperties(next,dishDto);

            //获取每条记录的菜品ID，以获取菜品名称
            Long categoryId = next.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            dishDtoList.add(dishDto);
        }
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    //回显功能，根据id查询相应的信息,
    @GetMapping("/{id}")
    public R<DishDto> getDishWithFlavrByid(@PathVariable("id") Long id){
        log.info("回显功能，本次回显Id的信息==={}",id);
        DishDto dishWithFlavorById = dishService.getDishWithFlavorById(id);

        return R.success(dishWithFlavorById);
    }

    //更新菜品功能
    @PutMapping
    public R<String> upDate(@RequestBody DishDto dishDto){
        log.info("更新菜品，其菜品的名称====={}",dishDto.getName());
        dishService.updateWithFlavor(dishDto);
        return R.success("保存菜品成功");
    }
}
