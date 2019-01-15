package com.manage.config;

import com.manage.auth.properties.RestProperties;
import com.manage.interceptor.AuthFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 是否开启jwt鉴权
 * @author luya
 * @created 2018-05-26
 */
@Configuration
public class WebConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = RestProperties.REST_PREFIX, name = "auth-open", havingValue = "true", matchIfMissing = true)
    public AuthFilter jwtAuthenticationTokenFilter() {
        return new AuthFilter();
    }

}
