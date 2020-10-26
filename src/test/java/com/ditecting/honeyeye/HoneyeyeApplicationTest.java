package com.ditecting.honeyeye;

import com.ditecting.honeyeye.pcap4j.extension.utils.TsharkUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/5/28 20:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class HoneyeyeApplicationTest {

    @Test
    public void test(){
        for(int a=0; a<10; a++){
            long start = currentTimeMillis();
            String command = "\"F:\\Program Files\\Wireshark\\tshark.exe\" -T json -r \"C:\\Users\\18809\\Desktop\\Honeyeye A Network Traffic Collection Framework for Distributed Honeynets of Industrial Control Systems\\experiment\\expB0\\C_10000.pcap\"";
            TsharkUtil.executeCommand(command);
            long finish = currentTimeMillis();
            System.out.println(finish-start);
        }
    }

    @Test
    public void testUDT () {

    }

}