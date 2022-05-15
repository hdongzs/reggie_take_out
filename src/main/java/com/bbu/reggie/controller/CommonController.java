package com.bbu.reggie.controller;

import com.bbu.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController()
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String path;

    /**
     * 文件图片的上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //获得文件原始名称
        String originalName = file.getOriginalFilename();
        //获得文件的后缀名
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        //生成新的文件名
        String fileName = UUID.randomUUID()+suffix;
        File localFile = new File(path);
        if(!localFile.exists()){
            localFile.mkdir();
        }
        try {
            file.transferTo(new File(path+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件图片的下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        response.setContentType("image/jpeg");
        InputStream in = null;
        try {
            in = new FileInputStream(path+name);
            OutputStream out = response.getOutputStream();
            int len = 0;
            byte [] read = new byte[1024];
            while((len = in.read(read))!=-1){
                out.write(read,0,len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
