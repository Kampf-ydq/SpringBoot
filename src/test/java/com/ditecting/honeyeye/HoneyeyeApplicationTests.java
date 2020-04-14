package com.ditecting.honeyeye;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@RunWith(SpringRunner.class)
@AutoConfigureMybatis
@SpringBootTest
class HoneyeyeApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Test");

//        byte [] rawData = {1,2,3};
//        TsharkUtil.executeTshark(rawData);
	}

}
