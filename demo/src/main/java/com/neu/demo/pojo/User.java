package com.neu.demo.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author ydq
 * @version 1.0
 * @date 2021/1/5 19:26
 */
/**
 * (1)结合注解@ConfigurationProperties(prefix = "star")将bean里面的属性和yml中的属性自动绑定
 * (2)再使用@Component注解表示把这个组件添加到容器中,如此才能生效
 */
@Component
@ConfigurationProperties(prefix = "star")
public class User {
    private String name;
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
