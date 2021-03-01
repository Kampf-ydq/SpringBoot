package com.neu.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerMessage {
    private String body;
    private String timestamp;
    private String threadName;
    private String className;
    private String level;

    //自定义toString函数，进行日志格式化

    @Override
    public String toString() {
        /*return "LoggerMessage{" +
                "body='" + body + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", threadName='" + threadName + '\'' +
                ", className='" + className + '\'' +
                ", level='" + level + '\'' +
                '}';*/

        String line = level; //日志等级
        line = line.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
        line = line.replace("INFO", "<span style='color: green;'>INFO</span>");
        line = line.replace("WARN", "<span style='color: orange;'>WARN</span>");
        line = line.replace("ERROR", "<span style='color: red;'>ERROR</span>");

        return timestamp + "&nbsp;&nbsp;" + line + "&nbsp;---&nbsp;" + "[" + threadName + "]"
                + "&nbsp;<span style='color: blue;'>" + className + "</span>&nbsp;&nbsp;&nbsp;"
                + ":&nbsp;" + body + "<br/>";
    }
}
