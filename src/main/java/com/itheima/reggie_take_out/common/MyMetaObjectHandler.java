package com.itheima.reggie_take_out.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author 陶月松
 * @create 2023-02-26 17:40
 * 自定义元数据对象处理器
 */

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    //每当遇到    @TableField(fill = FieldFill.INSERT)这个注解，就自动进行注入
    //这是一个创建新字段的自动填入
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]。。。。");
        log.info("需要创建自动注入的数据为：{}",metaObject);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrenId());
        metaObject.setValue("updateUser",BaseContext.getCurrenId());
    }


    //每当遇到        @TableField(fill = FieldFill.UPDATE)这个注解，就自动进行注入
    //这是一个更新字段的自动填入
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]。。。。");
        log.info("需要更新，自动注入的数据为：{}",metaObject.toString());

        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrenId());
    }
}
