package com.ditecting.honeyeye.pcap4j.extension.packet;

import com.ditecting.honeyeye.pcap4j.extension.utils.GsonUtils;
import com.ditecting.honeyeye.pcap4j.extension.utils.MatchProtocolEnum;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.namednumber.TcpPort;
import org.pcap4j.util.ByteArrays;

import java.util.Arrays;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Value
@Slf4j
public class ModbusTcpJsonPacket extends AbstractJsonPacket {
    private static final long serialVersionUID = 4109885831026621322L;

    private static final TcpPort myName = new TcpPort((short)502, "mbtcp");
    private static final List<String> myChildren = Arrays.asList("modbus");

    private final ModbusTcpJsonHeader header;

    public static ModbusTcpJsonPacket newPacket(String rawJsonPacket, byte[] rawData){
        return new ModbusTcpJsonPacket(rawJsonPacket, rawData);
    }

    private ModbusTcpJsonPacket(String rawJsonPacket, byte[] rawData){
        List<String> jsonStrArray = GsonUtils.getJsonPacket(rawJsonPacket, myName, true, myChildren);

        String flag = jsonStrArray.get(0);
        if(flag.equals(MatchProtocolEnum.UNMATCHED.name())){
            this.header = null;
        }else {
            StringBuilder sbHeader = new StringBuilder("{");

            for(int index = 1; index < jsonStrArray.size(); index++){
                sbHeader.append(jsonStrArray.get(index));
                sbHeader.append(",");
            }
            sbHeader.deleteCharAt(sbHeader.length() - 1);
            sbHeader.append("}");

            this.header = new ModbusTcpJsonHeader(sbHeader.toString(), rawData);
        }
    }

    public static ModbusTcpJsonPacket newPacket(byte[] rawData, int offset, int length) throws IllegalRawDataException {
        ByteArrays.validateBounds(rawData, offset, length);
        return new ModbusTcpJsonPacket(rawData, offset, length);
    }

    private ModbusTcpJsonPacket(byte[] rawData, int offset, int length) throws IllegalRawDataException{
        this.header = null;
    }

    private ModbusTcpJsonPacket(Builder builder) {
        if(builder != null){
            this.header = new ModbusTcpJsonPacket.ModbusTcpJsonHeader(builder);
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append("builder: ").append(builder);
            throw new NullPointerException(sb.toString());
        }
    }

    public ModbusTcpJsonHeader getHeader() { return header;}

    public ModbusTcpJsonPacket.Builder getBuilder() { return new ModbusTcpJsonPacket.Builder(this); }

    public static final class Builder extends AbstractBuilder{
        private String jsonHeader;
        private byte[] rawData;

        public Builder() {
        }

        public Builder(ModbusTcpJsonPacket packet) {
            this.jsonHeader = packet.header.jsonHeader;
            this.rawData = packet.header.rawData;
        }

        public Builder jsonHeader(String val) {
            jsonHeader = val;
            return this;
        }

        public Builder rawData(byte[] rawData) {
            this.rawData = rawData;
            return this;
        }

        public ModbusTcpJsonPacket build() {
            return new ModbusTcpJsonPacket(this);
        }
    }

    @ToString
    @EqualsAndHashCode
    public static final class ModbusTcpJsonHeader extends AbstractHeader{
        private static final long serialVersionUID = 8667687635206667375L;

        private final String jsonHeader;
        private final byte[] rawData;

        private ModbusTcpJsonHeader (String jsonHeader, byte[] rawData) {
            this.jsonHeader = jsonHeader;
            this.rawData = rawData;
        }

        private ModbusTcpJsonHeader(byte[] rawData, int offset, int lengt){
            this.jsonHeader = null;
            this.rawData = rawData;
        }

        private ModbusTcpJsonHeader(Builder builder){
            this.jsonHeader = builder.jsonHeader;
            this.rawData = builder.rawData;
        }

        @Override
        public byte[] getRawData() {
            return this.rawData;
        }

        @Override
        protected List<byte[]> getRawFields() {
            return null;
        }
    }


}
