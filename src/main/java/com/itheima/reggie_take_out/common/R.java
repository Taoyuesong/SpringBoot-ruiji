package com.itheima.reggie_take_out.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陶月松
 * @create 2023-02-24 15:07
 */
@Data
public class R<T>{
    private Integer code;
    private String msg;
    private T data;
    private Map map = new HashMap();

    public static <T> R<T> success(T object){
        R<T> r = new R<>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<T>();
        r.setMsg(msg);
        r.setCode(0);
        return r;
    }

    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
