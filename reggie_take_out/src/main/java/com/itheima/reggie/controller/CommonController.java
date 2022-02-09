package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //页面图片存取路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 存取用户上传文件
     *  multipartFile spring文件上传工具类
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //将文件重命名避免文件名相同覆盖
        String originalFilename = file.getOriginalFilename();      //获取原始文件名
        log.info(originalFilename);
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));    //文件类型
        String filename = UUID.randomUUID().toString() + suffix;    //新生成的文件名
        
        //获取文件存储目录
        File dir = new File(basePath);
        if(!dir.exists()){       //判断文件夹是否存在
            dir.mkdirs();    //创建多级文件夹
        }

        try {
            //将文件转存到指定存储目录
            file.transferTo(new File(basePath, filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filename);
    }

    /**
     * 加载图片
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        String suffix = name.substring(name.lastIndexOf("."));
        response.setContentType("image/"+suffix);

        BufferedInputStream bis = null;
        ServletOutputStream os = null;
        try {
            //创建输入流读取服务器本地存储的图片
            bis = new BufferedInputStream(new FileInputStream(new File(basePath, name)));
            //获取输出流将文件回写到页面
            os = response.getOutputStream();

            byte[] bytes = new byte[1024];      //byte数组作为中间容器
            int len;        //len存储每次接收到的数据量
            while((len = bis.read(bytes)) != -1){
                os.write(bytes, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bis != null)
                    bis.close();
                if(os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
