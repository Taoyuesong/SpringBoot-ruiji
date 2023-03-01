package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.entity.Category;

/**
 * @author 陶月松
 * @create 2023-02-26 19:24
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
