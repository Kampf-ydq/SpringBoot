package com.ditecting.honeyeye;

import com.ditecting.honeyeye.inputer.capturer.CaptureHolder;
import com.ditecting.honeyeye.inputer.loader.LoadHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
//@SpringBootApplication
public class HoneyeyeApplication implements CommandLineRunner {

    @Value("${honeyeye.system.inputingMode}")
    private int inputingMode;// 1:capture, 2:load

    @Autowired
    CaptureHolder captureHolder;

    @Autowired
    LoadHolder loadHolder;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HoneyeyeApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
	    switch (inputingMode){
            case 1: captureHolder.capture();break;
            case 2: loadHolder.load();break;
            default:
        }
	}
}
