package ru.serge2nd.samples.hib.atmks.config;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING_ARRAY;

public class ConfigHelper {
    private ConfigHelper() {}

    public static String[] propertyNames(PropertySource<?> ps) {
        return ps instanceof EnumerablePropertySource
                ? ((EnumerablePropertySource<?>)ps).getPropertyNames()
                : EMPTY_STRING_ARRAY;
    }
}