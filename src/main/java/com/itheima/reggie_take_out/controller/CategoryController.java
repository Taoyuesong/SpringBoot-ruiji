package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author 陶月松
 * @create 2023-02-26 19:32
 */

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    //增加分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("获取新增的菜品数据{}",category.toString());
        categoryService.save(category);
        return R.success("添加菜品成功");
    }
    //分页查询功能
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") Integer page,
                          @RequestParam("pageSize") Integer pageSize){
        Page<Category> pageCategory = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageCategory,lambdaQueryWrapper);
        return R.success(pageCategory);
    }

    //根据id进行修改
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("需要修改菜品的信息有{}",category.toString());
        categoryService.updateById(category);
        return R.success("修改菜品成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){
        log.info("删除菜品操作：删除ID：{}",id);

        //考虑菜品与具体菜有依赖性，每次删除时要考虑相应菜品下是否还有具体的菜名
        categoryService.remove(id);
        return R.success("删除分类成功");
    }

    //根据类型获得全部是类型
    @GetMapping("/list")

    //注意这里使用的是Category整体类去接收，type这一个参数。由于Category类中有相应的type这个值，可以直接赋值.
    public R<List<Category>> list(Category category){
        log.info("我们需要获得的类型为{}",category.toString());

        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

        List<Category> categories = categoryService.list(lambdaQueryWrapper);
        return R.success(categories);
    }

}
