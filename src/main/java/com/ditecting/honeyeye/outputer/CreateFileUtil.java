package com.ditecting.honeyeye.outputer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 21:46
 */
public class CreateFileUtil {

    public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        boolean flag = true;

        String fullPath = filePath + File.separator + fileName + ".json";

        try {
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

//            if(jsonString.indexOf("'")!=-1){
//                jsonString = jsonString.replaceAll("'", "\\'");
//            }
//            if(jsonString.indexOf("\"")!=-1){
//                jsonString = jsonString.replaceAll("\"", "\\\"");
//            }
//
//            if(jsonString.indexOf("\r\n")!=-1){
//                jsonString = jsonString.replaceAll("\r\n", "\\u000d\\u000a");
//            }
//            if(jsonString.indexOf("\n")!=-1){
//                jsonString = jsonString.replaceAll("\n", "\\u000a");
//            }

//            jsonString = JsonFormatTool.formatJson(jsonString);

            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }
}