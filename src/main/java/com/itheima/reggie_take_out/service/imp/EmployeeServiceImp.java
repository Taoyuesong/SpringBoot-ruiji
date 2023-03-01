package com.itheima.reggie_take_out.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.entity.Employee;
import com.itheima.reggie_take_out.mapper.EmployeeMapper;
import com.itheima.reggie_take_out.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 陶月松
 * @create 2023-02-24 15:24
 */
@Service
public class EmployeeServiceImp extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
