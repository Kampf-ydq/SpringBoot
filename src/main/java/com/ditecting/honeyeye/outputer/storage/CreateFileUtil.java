package com.ditecting.honeyeye.outputer.storage;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Scanner;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 21:46
 */
@Slf4j
public class CreateFileUtil {

    /**
     * write data to the end of an existing file.
     * @param rawData
     * @param filePath
     * @param fileName
     * @return
     */
    public static void writePcapFile(byte[] rawData, String filePath, String fileName) {
        try {
            if(!checkFilePath(filePath)){
                filePath = inputFilePath();
            }
            if(!checkFileName(fileName)){
                fileName = inputFileName();
            }

            String fullPath = filePath + File.separator + fileName + ".pcap";

            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                throw new IOException("The file [" + fullPath + "] does not exist.");
            }
            if (!file.exists()) {
                throw new IOException("The file [" + fullPath + "] does not exist.");
            }

            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(rawData);
            fos.flush();
            fos.close();
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create file. If the file has existed, it will be deleted.
     *
     * @param rawData
     * @param filePath
     * @param fileName
     * @return
     */
    public static void createPcapFile(byte[] rawData, String filePath, String fileName) {
        try {
            if(!checkFilePath(filePath)){
                filePath = inputFilePath();
            }
            if(!checkFileName(fileName)){
                fileName = inputFileName();
            }

            String fullPath = filePath + File.separator + fileName + ".pcap";

            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(rawData);
            fos.flush();
            fos.close();
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write data to the end of an existing file.
     * @param jsonString
     * @param filePath
     * @param fileName
     * @return
     */
    public static void writeJsonFile(String jsonString, String filePath, String fileName) {
        try {

            if(!checkFilePath(filePath)){
                filePath = inputFilePath();
            }
            if(!checkFileName(fileName)){
                fileName = inputFileName();
            }

            String fullPath = filePath + File.separator + fileName + ".json";

            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                throw new IOException("The file [" + fullPath + "] does not exist.");
            }
            if (!file.exists()) {
                throw new IOException("The file [" + fullPath + "] does not exist.");
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            long fileLength = randomAccessFile.length();
            randomAccessFile.seek(fileLength-1);//move point to the front of the last ']'
            StringBuilder sb = new StringBuilder();
            sb.append(",");
            sb.append(jsonString);
            sb.append("]");

            randomAccessFile.writeBytes(sb.toString());
            randomAccessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create file. If the file has existed, it will be deleted.
     *
     * @param jsonString
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        boolean flag = true;

        try {

            if(!checkFilePath(filePath)){
                filePath = inputFilePath();
            }
            if(!checkFileName(fileName)){
                fileName = inputFileName();
            }

            String fullPath = filePath + File.separator + fileName + ".json";

            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

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

    private static boolean checkFilePath(String filePath) {
        if (filePath == null){
            log.warn("The filePath is null.");
            return false;
        }

        if (filePath.equals("")){
            log.warn("The filePath is \"\".");
            return false;
        }

        return true;
    }

    private static boolean checkFileName(String fileName) {
        if (fileName == null){
            log.warn("The fileName is null.");
            return false;
        }

        if (fileName.equals("")){
            log.warn("The fileName is \"\".");
            return false;
        }

        return true;
    }

    private static String inputFilePath() throws IOException {
        Scanner scan = new Scanner(System.in);

        boolean validPath = false;
        int maxLoops = 3;

        /*read the file path in a loop*/
        String  filePath = null;
        while (! validPath && maxLoops > 0){
            System.out.println("please enter the output filePath");
            filePath = scan.nextLine();

            if(checkFilePath(filePath)){
                validPath = true;
                break;
            }
            maxLoops--;
        }

        if(! validPath){
            StringBuilder sb = new StringBuilder();
            sb.append("filePath: [").append(filePath).append("] is invalid!");
            throw new IOException(sb.toString());
        }
        return filePath;
    }

    private static String inputFileName() throws IOException {
        Scanner scan = new Scanner(System.in);

        boolean validPath = false;
        int maxLoops = 3;

        /*read the file path in a loop*/
        String fileName = null;
        while (!validPath && maxLoops > 0) {
            System.out.println("please enter the output fileName");
            fileName = scan.nextLine();

            if (checkFileName(fileName)) {
                validPath = true;
                break;
            }
            maxLoops--;
        }

        if (!validPath) {
            StringBuilder sb = new StringBuilder();
            sb.append("fileName: [").append(fileName).append("] is invalid!");
            throw new IOException(sb.toString());
        }

        return fileName;
    }
}