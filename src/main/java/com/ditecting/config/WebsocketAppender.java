package com.ditecting.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.ditecting.entity.LoggerMessage;
import com.ditecting.util.LoggerQueue;

import java.text.DateFormat;
import java.util.Date;

public class WebsocketAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {
        //System.out.println("WebsocketAppender begin...");
        try {
            LoggerMessage loggerMessage = new LoggerMessage(
                    event.getFormattedMessage(),
                    DateFormat.getDateTimeInstance().format(new Date(event.getTimeStamp())),
                    event.getThreadName(),
                    event.getLoggerName(),
                    event.getLevel().levelStr
            );
            LoggerQueue.getInstance().push(loggerMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
