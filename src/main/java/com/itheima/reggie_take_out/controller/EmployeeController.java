package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Employee;
import com.itheima.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author 陶月松
 * @create 2023-02-24 15:13
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    //登录功能
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,
                             @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employee1 = employeeService.getOne(employeeLambdaQueryWrapper);
        //        1. 其中Employee::getUsername的意思就相当于：
        //        1.1 实例化一个Employee对象
        //        Employee employee = new Employee();
        //        1.2 调用对象Employee的getUsername方法，这里调用的是getUsername:
        //        Employee.getUsername();
        //        2.eq方法相当于赋值“=”
        //        即将Username的值为参数employee.getUsername()，注意此时使用的是get方法而不是set方法

        //3、如果没有查询到则返回登录失败结果
        if (employee1 == null){
            return R.error("登录失败");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!employee1.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (employee1.getStatus() == 0){
            return R.error("账号已经被封禁");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",employee1.getId());
        return R.success(employee1);
    }

    //退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(@RequestBody Employee employee,
                          HttpServletRequest request){

        log.info("本次添加员工的信息为{}",employee);

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

        log.info("经过修改后的员工信息为{}",employee);

        boolean save = employeeService.save(employee);

        log.info("这次保存是否成功了:{}",save);
        //设置初始密码123456，需要进行md5加密处理

        //获得当前登录用户的id
        return R.success("新增员工成功");
    }

    //分页展示员工
    @GetMapping("/page")
    public R<Page> page(@PathParam("page") Integer page,
                        @PathParam("pageSize") Integer pageSize,
                        @PathParam("name") String name){


        log.info("获得页面信息page = {}，pageSize = {}，name = {}",page,pageSize,name);

        //构造分页构造器
        Page employeePage = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        //这里加了前置判断，若StringUtils.isNotEmpty(name)为true，才加这个条件
        queryWrapper.eq(StringUtils.isNotEmpty(name),Employee::getUsername,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        //这里会把查询的信息直接封装到employeePage的内部了
        employeeService.page(employeePage,queryWrapper);
        log.info("传输过去的数据{}",employeePage.getRecords());
        return R.success(employeePage);
    }

    //根据用户ID修改用户信息
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        log.info("接收到需要修改的信息{}",employee);

        employeeService.updateById(employee);
        return R.success("修改数据成功");
    }

    //根据用户ID查询用户信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id) {

        log.info("根据用户ID查询用户信息,用户ID{}",id);
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("该用户不存在");
    }
}
