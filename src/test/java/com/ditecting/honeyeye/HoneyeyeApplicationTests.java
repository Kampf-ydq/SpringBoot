package com.ditecting.honeyeye;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@AutoConfigureMybatis
@SpringBootTest
class HoneyeyeApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Test");
	}

}
