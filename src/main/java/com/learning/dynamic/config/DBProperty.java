package com.learning.dynamic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:DBProperties.properties")
public class DBProperty {

    @Autowired
    private Environment environment;

    public String getProperty(String propertyName) {
        return environment.getProperty(propertyName);
    }
}
