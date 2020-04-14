package com.ditecting.honeyeye;

import com.ditecting.honeyeye.capture.CaptureHolder;
import com.ditecting.honeyeye.load.LoadHolder;
import com.ditecting.honeyeye.load.LoadNote;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
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
@SpringBootApplication
@MapperScan ("com.ditecting.honeyeye.dao")
public class HoneyeyeApplication implements CommandLineRunner {

    @Autowired
    CaptureHolder captureHolder;

//    @Autowired
//    LoadHolder loadHolder;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HoneyeyeApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
        System.out.println("HoneyeyeApplication !!!");
        System.out.println("Start Capturing !!!");
//        captureHolder.capture();

        /*synchronously call LoadHolder*/
        new LoadHolder().load(null);
        log.info(LoadNote.printCounter());

        /* asynchronously call LoadHolder
        Runnable runLoad = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                log.info(Thread.currentThread().getName() + "runLoad");
                new LoadHolder().load("C:\\Users\\18809\\Desktop\\test\\test1.pcap");
                log.info(Thread.currentThread().getName() + " :" + LoadNote.printCounter());
            }
        };
        Thread thread = new Thread(runLoad);
        thread.start(); */

	}



}
