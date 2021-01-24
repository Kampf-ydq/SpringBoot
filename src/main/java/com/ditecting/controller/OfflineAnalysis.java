package com.ditecting.controller;

import com.ditecting.entity.HoneyResult;
import com.ditecting.entity.Honeyeye;
import com.ditecting.honeyeye.inputer.loader.LoadHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/***
 * 离线解析控制器
 */
@Controller
public class OfflineAnalysis {

    private String loadFilePath;

    @RequestMapping(value = "/fileUpload")
    public void fileupload(@RequestParam(value = "file") MultipartFile file){

        // 获取文件的原文件名
        String oldName = file.getOriginalFilename();
        // 文件上传，保存为新的文件名
        if (!"".equals(oldName) && oldName != null){
            // 获取文件后缀
            String suffixFileName = oldName.substring(oldName.lastIndexOf("."));

            // 设置文件保存的文件夹
            SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd");
            String filePath = Constants.FILE_UPLOAD_PATH + sdf.format(new Date()) + "/" + UUID.randomUUID() + suffixFileName;
            File dest = new File(filePath);
            // 判断文件夹是否存在
            if (!dest.exists()){
                dest.mkdirs();
            }
            try {
                // 文件写入
                file.transferTo(dest);
                // 缓存 文件上传成功之后的文件名路径
                loadFilePath = filePath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/offline", method = RequestMethod.POST)
    public HoneyResult offlineAly(@RequestBody Honeyeye honeyeye){
        System.out.println(honeyeye);
        System.out.println(loadFilePath);
        return HoneyResult.ok();
    }

}
