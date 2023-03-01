package com.itheima.reggie_take_out.controller;

import com.itheima.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author 陶月松
 * @create 2023-02-27 17:48
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //这里引用了动态配置路径模式
    @Value("${reggie.path}")
    private String basePath;
    //文件上传功能，返回值，需要生成的文件名，以便后面下载,
    //入参名称只能为file,是前端设置的name="file"
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        //javaJDK（1.5以上的版本）提供的一个自动生成主键的方法，它生成的是以为32位的数字和字母组合的字符，中间还参杂着4个 - 符号。
        String filename = UUID.randomUUID().toString() + suffix;

        //创建文件夹
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(@RequestParam("name") String filename,
                         HttpServletResponse response){
        String url = basePath + filename;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(url));

            //从响应中获取，输入流，以将文件输入到其中
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len = 0;

            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
                log.info("传输数据长度{}",len);
            }

            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
