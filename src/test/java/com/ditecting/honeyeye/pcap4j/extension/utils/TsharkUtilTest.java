package com.ditecting.honeyeye.pcap4j.extension.utils;

import org.junit.jupiter.api.Test;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 16:55
 */
class TsharkUtilTest {

    @Test
    void executeTshark() {
        byte [] rawData = {1,2,3};
        TsharkUtils.executeTshark(rawData);
    }

    @Test
    void getTsharkUtil() {
    }
}