package com.sleepystack.bankingapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Configuration
public class InfoPropertiesDebugConfig {

    @Autowired
    private ConfigurableEnvironment env;

    @EventListener(ApplicationReadyEvent.class)
    public void printInfoProperties() {
        System.out.println("==== INFO PROPERTIES ====");
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            if (propertySource.getSource() instanceof java.util.Map<?, ?> map) {
                for (Object keyObj : map.keySet()) {
                    String key = String.valueOf(keyObj);
                    if (key.startsWith("info")) {
                        System.out.println(key + " = " + map.get(key));
                    }
                }
            }
        }
        System.out.println("=========================");
    }
}