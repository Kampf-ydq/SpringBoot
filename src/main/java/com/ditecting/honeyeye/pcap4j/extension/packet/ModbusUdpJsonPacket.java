package com.ditecting.honeyeye.pcap4j.extension.packet;

import com.ditecting.honeyeye.pcap4j.extension.utils.GsonUtils;
import com.ditecting.honeyeye.pcap4j.extension.utils.MatchProtocolEnum;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.namednumber.UdpPort;
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
public class ModbusUdpJsonPacket extends AbstractJsonPacket {
    private static final long serialVersionUID = -1152433515422394641L;

    private static final UdpPort myName = new UdpPort((short)502, "mbudp");
    private static final List<String> myChildren = Arrays.asList("modbus");


    private final ModbusUdpJsonHeader header;

    public static ModbusUdpJsonPacket newPacket(String rawJsonPacket, byte[] rawData){
        return new ModbusUdpJsonPacket(rawJsonPacket, rawData);
    }

    private ModbusUdpJsonPacket(String rawJsonPacket, byte[] rawData){
        List<String> jsonArray = GsonUtils.getJsonPacket(rawJsonPacket, myName, true, myChildren);
        String flag = jsonArray.get(0);
        if(flag.equals(MatchProtocolEnum.UNMATCHED.name())){
            this.header = null;
        }else {
            StringBuilder sbHeader = new StringBuilder("{");
            for (String json : jsonArray) {
                sbHeader.append(json);
                sbHeader.append(",");
            }
            sbHeader.deleteCharAt(sbHeader.length() - 1);
            sbHeader.append("}");

            this.header = new ModbusUdpJsonHeader(sbHeader.toString(), rawData);
        }
    }

    public static ModbusUdpJsonPacket newPacket(byte[] rawData, int offset, int length) throws IllegalRawDataException {
        ByteArrays.validateBounds(rawData, offset, length);
        return new ModbusUdpJsonPacket(rawData, offset, length);
    }

    private ModbusUdpJsonPacket(byte[] rawData, int offset, int length) throws IllegalRawDataException{
        this.header = null;
    }

    private ModbusUdpJsonPacket(Builder builder) {
        if(builder != null){
            this.header = new ModbusUdpJsonPacket.ModbusUdpJsonHeader(builder);
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append("builder: ").append(builder);
            throw new NullPointerException(sb.toString());
        }
    }

    public ModbusUdpJsonHeader getHeader() { return header;}

    public ModbusUdpJsonPacket.Builder getBuilder() { return new ModbusUdpJsonPacket.Builder(this); }

    public static final class Builder extends AbstractBuilder{
        private String jsonHeader;
        private byte[] rawData;

        public Builder() {
        }

        public Builder(ModbusUdpJsonPacket packet) {
            this.jsonHeader = packet.header.jsonHeader;
            this.rawData = packet.header.rawData;
        }

        public Builder jsonHeader(String val) {
            jsonHeader = val;
            return this;
        }

        public Builder rawData (byte[] rawData){
            this.rawData = rawData;
            return this;
        }


        public ModbusUdpJsonPacket build() {
            return new ModbusUdpJsonPacket(this);
        }
    }

    @ToString
    @EqualsAndHashCode
    public static final class ModbusUdpJsonHeader extends AbstractHeader{

        private static final long serialVersionUID = 1879792801070459638L;
        private final String jsonHeader;
        private final byte[] rawData;

        private ModbusUdpJsonHeader (String jsonHeader, byte[] rawData) {
            this.jsonHeader = jsonHeader;
            this.rawData = rawData;
        }

        private ModbusUdpJsonHeader(byte[] rawData, int offset, int lengt){
            this.jsonHeader = null;
            this.rawData = rawData;
        }

        private ModbusUdpJsonHeader(Builder builder){
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
