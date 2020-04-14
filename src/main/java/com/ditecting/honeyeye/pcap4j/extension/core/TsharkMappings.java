package com.ditecting.honeyeye.pcap4j.extension.core;

import com.ditecting.honeyeye.capture.CaptureHolder;
import com.ditecting.honeyeye.load.LoadHolder;
import com.sun.jna.Structure;
import lombok.Builder;
import lombok.Getter;
import org.pcap4j.core.NativeMappings;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.util.ByteArrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.ByteOrder;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 19:52
 */
@Component
public class TsharkMappings {
    public static final ByteOrder NATIVE_BYTE_ORDER = ByteOrder.nativeOrder();
//    public static final ByteOrder WRITE_BYTE_ORDER = ByteOrder.BIG_ENDIAN; // ByteOrder.BIG_ENDIAN by default
    private static DataLinkType dlt = DataLinkType.NULL;

    @Autowired
    CaptureHolder captureHolder;
    private static CaptureHolder staticCaptureHolder;

    @Autowired
    LoadHolder loadHolder;
    private static LoadHolder staticLoadHolder;

    @PostConstruct
    public void init() {
        staticCaptureHolder = captureHolder;
        staticLoadHolder = loadHolder;
    }

    public static DataLinkType getDlt(){
        if(dlt.value() == DataLinkType.NULL.value()){
            /*only when the system starts to capture or load data, dlt will get the real DataLinkType*/
            synchronized(dlt){
                if(staticCaptureHolder.getHandle() != null){
                    dlt = staticCaptureHolder.getHandle().getDlt();
                }else if(staticLoadHolder.getHandle() != null){
                    dlt = staticLoadHolder.getHandle().getDlt();
                }
            }
        }
        return dlt;
    }

    @Getter
    @Builder
    public static class PcapFileHeader {
        private int magic;
        private short magorVersion;
        private short minorVersion;
        private int timezone;
        private int sigflags;
        private int snaplen;
        private int linktype;

        public PcapFileHeader(int magic, short magorVersion, short minorVersion, int timezone, int sigflags, int snaplen, int linktype) {
            this.magic = magic;
            this.magorVersion = magorVersion;
            this.minorVersion = minorVersion;
            this.timezone = timezone;
            this.sigflags = sigflags;
            this.snaplen = snaplen;
            this.linktype = linktype;
        }

        public  byte[] toByteArray(){
            byte[] magicArray = ByteArrays.toByteArray(magic);
            byte[] magorVersionArray = ByteArrays.toByteArray(magorVersion);
            byte[] minorVersionArray = ByteArrays.toByteArray(minorVersion);
            byte[] timezoneArray = ByteArrays.toByteArray(timezone);
            byte[] sigflagsArray = ByteArrays.toByteArray(sigflags);
            byte[] snaplenArray = ByteArrays.toByteArray(snaplen);
            byte[] linktypeArray = ByteArrays.toByteArray(linktype);

            int countLength = 0;
            byte[] pfhByteArray = new byte[24];
            System.arraycopy(magicArray, 0, pfhByteArray, countLength, magicArray.length);
            countLength += magicArray.length;
            System.arraycopy(magorVersionArray, 0, pfhByteArray, countLength, magorVersionArray.length);
            countLength += magorVersionArray.length;
            System.arraycopy(minorVersionArray, 0, pfhByteArray, countLength, minorVersionArray.length);
            countLength += minorVersionArray.length;
            System.arraycopy(timezoneArray, 0, pfhByteArray, countLength, timezoneArray.length);
            countLength += timezoneArray.length;
            System.arraycopy(sigflagsArray, 0, pfhByteArray, countLength, sigflagsArray.length);
            countLength += sigflagsArray.length;
            System.arraycopy(snaplenArray, 0, pfhByteArray, countLength, snaplenArray.length);
            countLength += snaplenArray.length;
            System.arraycopy(linktypeArray, 0, pfhByteArray, countLength, linktypeArray.length);

            return pfhByteArray;
        }
    }

    @Builder
    public static class PcapDataHeader {
        private int timeS;
        private int timeMs;
        private int caplen;
        private int len;

        public PcapDataHeader (byte[] hd) {
            int offset = 0;
            timeS = ByteArrays.getInt(hd, offset);
            offset += 4;
            timeMs = ByteArrays.getInt(hd, offset);
            offset += 4;
            caplen = ByteArrays.getInt(hd, offset);
            offset += 4;
            len = ByteArrays.getInt(hd, offset);
        }

        public PcapDataHeader(int timeS, int timeMs, int caplen, int len) {
            this.timeS = timeS;
            this.timeMs = timeMs;
            this.caplen = caplen;
            this.len = len;
        }

        public  byte[] toByteArray(){
            byte[] timeSArray = ByteArrays.toByteArray(timeS);
            byte[] timeMsArray = ByteArrays.toByteArray(timeMs);
            byte[] caplenArray = ByteArrays.toByteArray(caplen);
            byte[] lenArray = ByteArrays.toByteArray(len);

            int countLength = 0;
            byte[] pdhByteArray = new byte[16];
            System.arraycopy(timeSArray, 0, pdhByteArray, countLength, timeSArray.length);
            countLength += timeSArray.length;
            System.arraycopy(timeMsArray, 0, pdhByteArray, countLength, timeMsArray.length);
            countLength += timeMsArray.length;
            System.arraycopy(caplenArray, 0, pdhByteArray, countLength, caplenArray.length);
            countLength += caplenArray.length;
            System.arraycopy(lenArray, 0, pdhByteArray, countLength, lenArray.length);
            countLength += lenArray.length;

            return pdhByteArray;
        }
    }

    }