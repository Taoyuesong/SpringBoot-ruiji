package com.itheima.reggie_take_out.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.dto.DishDto;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.DishFlavor;
import com.itheima.reggie_take_out.mapper.DishMapper;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.DisFlavorService;
import com.itheima.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * @author 陶月松
 * @create 2023-02-27 10:08
 */
@Service
@Slf4j
public class DishServiceImp extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DisFlavorService disFlavorService;

    //保存带口味的菜品，由于涉及到两张表的增添，所以就要使用事务管理了
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish，由于DIshDto中包含了dish的所有基本信息，所以可以直接使用
        this.save(dishDto);
        //这里有两处疑问：第一，将基础信息保存过去，那此时的ID是通过java的API中生成的，还是有数据生成的
        //第二，若是通过数据库生成的ID，那么执行save，还有个回调的过程吗？要不然，怎么获得ID的呢？

        //其中ID是有MyBatis-plus自动生成的，通过内置的雪花算法生成的，这里并没有执行回调，就是在原有的数据进行了一个保存。
        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();


        //这里通过迭代器中，可知从iterator.next()中获取的其实是数据的引用。
        Iterator<DishFlavor> iterator = flavors.iterator();
        while (iterator.hasNext()){
            DishFlavor next = iterator.next();
            next.setDishId(id);
        }
        log.info("所有菜品口味：：{}",flavors.toString());

        //批量保存
        disFlavorService.saveBatch(flavors);

    }


    //个人理解这里虽然涉及到两张表的操作，但都是查询。是否需要加事务，有待商榷
    @Override
    @Transactional
    public DishDto getDishWithFlavorById(Long id) {

        DishDto dishDto = new DishDto();
        Dish byId = this.getById(id);
        BeanUtils.copyProperties(byId,dishDto);

        Long dishId = byId.getId();

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

        List<DishFlavor> dishFlavorList = disFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //由于dishDto是Dish的子类，所以也可以直接进行更新
        this.updateById(dishDto);
        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        //首先，对在dish品类下的所有口味先删除

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        disFlavorService.remove(lambdaQueryWrapper);

        //在把dishDto中的口味加进去
        Iterator<DishFlavor> iterator = flavors.iterator();
        while (iterator.hasNext()){
            DishFlavor next = iterator.next();
            next.setDishId(id);
        }

        disFlavorService.saveBatch(flavors);


    }
}
