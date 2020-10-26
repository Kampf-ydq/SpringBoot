package com.ditecting.honeyeye.cachepool;

import com.ditecting.honeyeye.converter.datatype.PacketMeeting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/4 10:28
 */

@Component
@Slf4j
public class ConverterCachePool {
    private Queue<PacketMeeting> cachePool = new LinkedList<>();
    private boolean status = false; //false:continue, true:end

    public boolean getStatus () {
        return status;
    }

    public void setStatus (boolean status) {
        this.status = status;
    }

    public void add (PacketMeeting pm) {
        cachePool.offer(pm);
//        log.info("A meeting is added to the ConverterCachePool.");
    }

    public boolean isEmpty () {
        return cachePool.isEmpty();
    }

    public PacketMeeting remove () {
        PacketMeeting pm = cachePool.poll();
//        log.info("A meeting is removed from the ConverterCachePool.");
        return pm;
    }
}