package com.ditecting.honeyeye.pcap4j.extension.core;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.util.ByteArrays;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 19:52
 */
@Slf4j
@Component
public class TsharkMappings {

    public static PcapFileHeader generateDefaultPcapFileHeader () {
        PcapFileHeader pcapFileHeader = TsharkMappings.PcapFileHeader.builder()
                .magic(-725372255)
                .magorVersion((short)512)
                .minorVersion((short)1024)
                .timezone(0)
                .sigflags(0)
                .snaplen(65535)
                .linktype(16777216)
                .build();
//        log.info("PcapFileHeader: " + ByteArrays.toHexString(pcapFileHeader.toByteArray(), ""));

        return pcapFileHeader;
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

    public static class PcapDataHeader {
        private final ByteOrder byteOrder;
        private int timeS;
        private int timeMs;
        private int caplen;
        private int len;

        public PcapDataHeader (byte[] hd, ByteOrder byteOrder) {
            this.byteOrder = byteOrder;

            int offset = 0;
            timeS = ByteArrays.getInt(hd, offset, byteOrder);
            offset += 4;
            timeMs = ByteArrays.getInt(hd, offset, byteOrder);
            offset += 4;
            caplen = ByteArrays.getInt(hd, offset, byteOrder);
            offset += 4;
            len = ByteArrays.getInt(hd, offset, byteOrder);
        }

        public  byte[] toByteArray(){
            byte[] timeSArray = ByteArrays.toByteArray(timeS, byteOrder);
            byte[] timeMsArray = ByteArrays.toByteArray(timeMs, byteOrder);
            byte[] caplenArray = ByteArrays.toByteArray(caplen, byteOrder);
            byte[] lenArray = ByteArrays.toByteArray(len, byteOrder);

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

        public double getTime(){
            double time = timeMs;
            return time/1000000 + timeS;
        }
    }

}