package com.genersoft.iot.vmp.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * <p>
 * 上传文件
 * @date 2020-07-29
 */
@Service
public class UploadUtil {
    //处理文件上传的方法
    public String upload(MultipartFile file, HttpServletRequest request, String realPath) throws IOException {
        //文件名我这里使用UUID和时间组成的
        String newFileNamePrefix= UUID.randomUUID().toString().replace("-","");
        String newFileName=newFileNamePrefix+".jpg";
        //处理文件上传
        file.transferTo(new File(realPath,newFileName));

        return newFileName;
    }

    public void download(String filename, HttpServletResponse res) throws IOException {
        // 发送给客户端的数据
        OutputStream outputStream = res.getOutputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        // 读取filename
        bis = new BufferedInputStream(new FileInputStream(new File("C:\\testUpfiles" + "\\/" +filename)));
        int i = bis.read(buff);
        while (i != -1) {
            outputStream.write(buff, 0, buff.length);
            outputStream.flush();
            i = bis.read(buff);
        }
    }
}
