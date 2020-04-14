package com.ditecting.honeyeye.load;

import com.ditecting.honeyeye.pcap4j.extension.core.FullPcapHandle;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.pcap4j.util.ByteArrays;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Scanner;

/**
 * LoadHolder ByteOrder is consistent with System ByteOrder.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/31 22:01
 */
@Slf4j
@Component
public class LoadHolder {
    private FullPcapHandle handle = null;

    public FullPcapHandle getHandle() {
        return handle;
    }

    public void load(String filePath) throws IOException {
        try {
            if(filePath == null) {
                filePath = openPcapFile();
            }

            PcapHandle ph = Pcaps.openOffline(filePath);

            generatePcapFileHeader(filePath);
            ByteOrder byteOrder = null;
            if(FullPacketPool.pcapFileHeader.get() == null){
                throw new NullPointerException("PcapFileHeader in ConvertListener is null");
            }else{
                if(FullPacketPool.pcapFileHeader.get().getMagic() == 0xa1b2c3d4){
                    byteOrder = ByteOrder.BIG_ENDIAN;
                }
                if(FullPacketPool.pcapFileHeader.get().getMagic() == 0xd4c3b2a1){
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
            }
            if(byteOrder == null){
                throw new IOException("the file [" + filePath + "] is not a valid .pcap file.");
            }
            LoadListener loadListener = new LoadListener();

            handle = new FullPcapHandle(ph.getHandle(), ph.getTimestampPrecision(), byteOrder);
            handle.loop(-1, loadListener);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NotOpenException e) {
            e.printStackTrace();
        }
        FullPacketPool.riffleFullPacketPool();
    }

    private String openPcapFile() throws IOException {
        Scanner scan = new Scanner(System.in);

        boolean validPath = false;
        int maxLoops = 3;

        /*read the file path in a loop*/
        String  filePath = null;
        while (! validPath && maxLoops > 0){
            System.out.println("please enter the file path of a .pcap file：");
            filePath = scan.nextLine();
            if(filePath == null){
                maxLoops--;
                continue;
            }
            File file = new File(filePath);
            if (! file.exists()){
                maxLoops--;
                continue;
            }
            validPath = true;
        }

        if(! validPath){
            StringBuilder sb = new StringBuilder();
            sb.append("filePath: ").append(filePath).append(" is invalid!");
            throw new IOException(sb.toString());
        }

        return filePath;
    }

    private void generatePcapFileHeader (String filePath) {
        FileInputStream fis = null;
        byte[] file_header = new byte[24];
        int headerLength = 24;
        try {
            fis = new FileInputStream(filePath);
            int m = fis.read(file_header, 0, headerLength-1);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        log.info("file_header: " + ByteArrays.toHexString(file_header, ""));

        int offset = 0;
        int magic = ByteArrays.getInt(file_header, offset);
        offset += 4;

        short magorVersion = ByteArrays.getShort(file_header, offset);
        offset += 2;

        short minorVersion = ByteArrays.getShort(file_header, offset);
        offset += 2;

        int timezone = ByteArrays.getInt(file_header, offset);
        offset += 4;

        int sigflags = ByteArrays.getInt(file_header, offset);
        offset += 4;

        int snaplen = ByteArrays.getInt(file_header, offset);
        offset += 4;

        int linktype = ByteArrays.getInt(file_header, offset);

        TsharkMappings.PcapFileHeader pcapFileHeader = TsharkMappings.PcapFileHeader.builder()
                .magic(magic)
                .magorVersion(magorVersion)
                .minorVersion(minorVersion)
                .timezone(timezone)
                .sigflags(sigflags)
                .snaplen(snaplen)
                .linktype(linktype)
                .build();
//        log.info("PcapFileHeader: " + ByteArrays.toHexString(pcapFileHeader.toByteArray(), ""));

        if(FullPacketPool.pcapFileHeader.get() == null){
            FullPacketPool.pcapFileHeader.set(pcapFileHeader);
        }
    }
}