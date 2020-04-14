package com.ditecting.honeyeye.pcap4j.extension.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 15:46
 */
@Slf4j
public class TsharkUtils {

    private static String tsharkPath;

    public TsharkUtils(Properties properties) {
        /*the file path with some spaces has to be surrounded with "" in the cmd command.*/
        String path = properties.getProperty("pcap4j.extension.util.tsharkPath");
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(path);
        sb.append("\"");
        this.tsharkPath = sb.toString();
    }

    /**
     * call tshark to dissect tempPcapFile.
     *
     * @param tempPcapFile tempPcapFile
     *
     * @return dissected result
     */
    public static String executeTshark(@NonNull File tempPcapFile){

        /*generate command*/
        String outType = " -T json";
        String tempPath = null;
        try {
            tempPath = tempPcapFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String command = tsharkPath + outType + " -r "+ " \"" + tempPath + "\"";

        Map<String, Object> cmdMap = executeCommand(command);

        String result = null;
        int status = (Integer) cmdMap.get("status");
        if(status == 0){//command succeeded
            result = (String) cmdMap.get("result");
        }

        return result;
    }

    /**
     * convert rawData into temp .pcap file and call tshark to dissect it.
     *
     * @param rawData rawData
     *
     * @return dissected result
     */
    public static String executeTshark(@NonNull byte[] rawData){
        try {
            /*generate temp .pcap file with rawData*/
            String tempName = (new Date()).getTime() + "";
            final File tempPcapFile = File.createTempFile(tempName, ".pcap");
            FileOutputStream fos = new FileOutputStream(tempPcapFile);
            fos.write(rawData);
            fos.flush();
            fos.close();

            /*generate command*/
            String outType = " -T json";
            String tempPath = tempPcapFile.getCanonicalPath();
            String command = tsharkPath + outType + " -r "+ " \"" + tempPath + "\"";

            Map<String, Object> cmdMap = executeCommand(command);

            String result = null;
            int status = (Integer) cmdMap.get("status");
            if(status == 0){//command succeeded
                result = (String) cmdMap.get("result");
            }

            /*delete temp .pcap file*/
            tempPcapFile.delete();

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * execute tshark command
     *
     * @param command command
     *
     * @return dissected result
     */
    private static Map executeCommand(String command){
        Date date = new Date();
        long commandId = date.getTime();
//        log.info("the command["+ commandId +"] start: "+command);

        Map<String, Object> resultMap = new HashMap();
        Runtime runtime = Runtime.getRuntime();
        Process p = null;
        try {
            p = runtime.exec(command);
            StringBuilder sbInput = new StringBuilder();
            InputStream isInput = p.getInputStream();
            BufferedReader brInput = new BufferedReader(new InputStreamReader(isInput));
            try {
                String line1 = null;
                while ((line1 = brInput.readLine()) != null) {
                    if (line1 != null){
                        sbInput.append(line1);
                    }
                }
//               log.info(sbInput.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                try {
                    isInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder sbError = new StringBuilder();
            InputStream isError = p.getErrorStream();
            BufferedReader brError = new  BufferedReader(new  InputStreamReader(isError));
            try {
                String line2 = null ;
                while ((line2 = brError.readLine()) !=  null ) {
                    if (line2 != null){
                        sbError.append(line2);
                    }
                }
//                log.info(sbError.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                try {
                    isError.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int status = p.waitFor();
            resultMap.put("status", status);
            if (status != 0){
//                throw new WinTsharkException("the command["+ commandId +"] failed: "+sbError.toString(), status);
                log.error("the command["+ commandId +"] failed: "+sbError.toString());
                resultMap.put("result", sbError.toString());
            }else{
//                log.info("the command["+ commandId +"] has been executed successfully.");
                resultMap.put("result", sbInput.toString());
            }
            p.destroy();
        } catch (Exception e) {
            try {
                p.getErrorStream().close();
                p.getInputStream().close();
                p.getOutputStream().close();
            } catch (Exception ee) {}
            e.printStackTrace();
        }

        return resultMap;
    }
}