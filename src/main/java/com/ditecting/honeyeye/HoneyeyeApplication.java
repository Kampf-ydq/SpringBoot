package com.ditecting.honeyeye;

import com.ditecting.honeyeye.capture.Captureholder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan ("com.ditecting.honeyeye.dao")
public class HoneyeyeApplication implements CommandLineRunner {

    @Autowired
    Captureholder captureholder;


	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HoneyeyeApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
        System.out.println("HoneyeyeApplication !!!");
        System.out.println("Start Capturing !!!");
        captureholder.capture();
	}

}
