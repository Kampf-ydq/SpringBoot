package com.ditecting.controller;

import com.ditecting.entity.*;
import com.ditecting.honeyeye.cachepool.InputCachePool;
import com.ditecting.honeyeye.inputer.loader.LoadHolder;
import com.ditecting.honeyeye.listener.ConvertingListener;
import com.ditecting.honeyeye.listener.LoadingListener;
import com.ditecting.honeyeye.listener.OutputtingListener;
import com.ditecting.honeyeye.listener.TransmittingListener;
import org.springframework.beans.factory.annotation.Autowired;
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

    //文件上传成功之后的文件名路径
    private String filePathOfLoad = "";

    /***
     *底层参数注入
     */
    @Autowired
    private InputCachePool icp;

    @Autowired
    private LoadingListener ll;

    @Autowired
    private ConvertingListener cl;

    @Autowired
    private TransmittingListener tl;

    @Autowired
    private OutputtingListener ol;

    @Autowired
    LoadHolder loadHolder;

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    @ResponseBody
    public HoneyResult fileupload(@RequestParam(value = "file", required = false) MultipartFile file){
        if (file.isEmpty()){
            return HoneyResult.build(500, "文件不能为空", null);
        }

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
                filePathOfLoad = filePath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return HoneyResult.ok();
    }

    @RequestMapping(value = "/offline", method = RequestMethod.POST)
    @ResponseBody
    public HoneyResult offlineAly(@RequestBody Honeyeye honeyeye){
        System.out.println(honeyeye);

        //配置参数
        SystemMode sys = honeyeye.getSystem();
        Listener listener = honeyeye.getListener();
        Outputer outputer = honeyeye.getOutputer();

        loadHolder.setFilePath(filePathOfLoad);
        loadHolder.setSegmentMax(listener.getSegmentMax());
        loadHolder.setOutputingMode(sys.getOutputingMode());

        /*=====================================*\
        *=    不能重新new对象，必须要注入底层对象  =*
         *======================================*/
        //InputCachePool icp = new InputCachePool();
        icp.setMeetingTimeout(listener.getMeetingTimeout());
        icp.setSegmentMax(listener.getSegmentMax());
        loadHolder.setInputCachePool(icp);

        //LoadingListener ll = new LoadingListener();
        ll.setInputCachePool(icp);
        loadHolder.setLoadingListener(ll);

        //ConvertingListener cl = new ConvertingListener();
        cl.setTransmittingGrain(listener.getTransmittingGrain());
        cl.setTransmittingTimeout(listener.getTransmittingTimeout());
        cl.setOutputtingGrain(listener.getOutputtingGrain());
        cl.setOutputtingTimeout(listener.getOutputtingTimeout());
        cl.setPluginGrain(listener.getPluginGrain());
        cl.setPluginTimeout(listener.getPluginTimeout());
        cl.setInputingMode(2); //输入模式设置为离线解析
        cl.setOutputingMode(sys.getOutputingMode());
        loadHolder.setConvertingListener(cl);

        //TransmittingListener tl = new TransmittingListener();
        tl.setInputingMode(2);
        tl.setPort(listener.getTransmittingListener().getPort());
        tl.setNetAddress(listener.getTransmittingListener().getNetAddress());
        tl.setTransmittingGrain(listener.getTransmittingGrain());
        loadHolder.setTransmittingListener(tl);

        //OutputtingListener ol = new OutputtingListener();
        ol.setOutputtingGrain(listener.getOutputtingGrain());
        ol.setInputingMode(2);
        ol.setFilePath(outputer.getFilePath());
        ol.setFileName(outputer.getFileName());
        loadHolder.setOutputtingListener(ol);

        System.out.println(loadHolder);

        try{
            loadHolder.load();
        }catch (Exception e){
            e.printStackTrace();
        }

        return HoneyResult.ok();
    }

}
