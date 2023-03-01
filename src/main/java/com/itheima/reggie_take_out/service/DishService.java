package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.dto.DishDto;
import com.itheima.reggie_take_out.entity.Dish;

/**
 * @author 陶月松
 * @create 2023-02-27 10:07
 */
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getDishWithFlavorById(Long id);

    void updateWithFlavor(DishDto dishDto);
}
