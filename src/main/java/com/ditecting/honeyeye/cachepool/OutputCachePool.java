package com.ditecting.honeyeye.cachepool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/4 10:28
 */

@Component
@Slf4j
public class OutputCachePool {
    private Queue<String> cachePool = new LinkedList<>();
    private boolean status = false; //false:continue, true:end

    public boolean getStatus () {
        return status;
    }

    public void setStatus (boolean status) {
        this.status = status;
    }

    public void addAll (List<String> strings) {
        cachePool.addAll(strings);
//        log.info("An item is added to the OutputCachePool.");
    }

    public void add (String string) {
        cachePool.offer(string);
//        log.info("An item is added to the OutputCachePool.");
    }

    public boolean isEmpty () {
        return cachePool.isEmpty();
    }

    public String remove () {
        String string = cachePool.poll();
//        log.info("An item is removed from the OutputCachePool.");
        return string;
    }
}