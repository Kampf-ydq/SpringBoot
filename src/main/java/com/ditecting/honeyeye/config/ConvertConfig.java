package com.ditecting.honeyeye.config;

import com.ditecting.honeyeye.pcap4j.extension.utils.TsharkUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/31 21:04
 */
@Configuration
public class ConvertConfig {
    @Bean
    public TsharkUtils tsharkUtil() {
        Resource app = new ClassPathResource("application-dev.yml");
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(app);
        Properties properties = yamlPropertiesFactoryBean.getObject();
        TsharkUtils tsharkUtil = new TsharkUtils(properties);
        return tsharkUtil;
    }

}