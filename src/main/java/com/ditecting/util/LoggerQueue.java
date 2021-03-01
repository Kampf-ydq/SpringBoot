package com.ditecting.util;

import com.ditecting.entity.LoggerMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerQueue {
        // Queue size
        public static final int QUEUE_MAX_SIZE = 10000;

        private static LoggerQueue alarmMessageQueue = new LoggerQueue();

        // Block queue
        private BlockingQueue blockingQueue = new LinkedBlockingQueue(QUEUE_MAX_SIZE);

        private LoggerQueue() {
        }

        public static LoggerQueue getInstance() {
            return alarmMessageQueue;
        }
        /**
         *  Push the message to queue
         * @param log
         * @return
         */
        public boolean push(LoggerMessage log) {
            // When the queue is fulled, then not block
            return this.blockingQueue.add(log);//队列满了就抛出异常，不阻塞
        }
        /**
         *  Poll the message to queue
         * @return
         */
        public LoggerMessage poll() {
            LoggerMessage result = null;
            try {
                result = (LoggerMessage) this.blockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }
}
