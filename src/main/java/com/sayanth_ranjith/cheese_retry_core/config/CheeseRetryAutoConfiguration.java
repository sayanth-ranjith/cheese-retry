package com.sayanth_ranjith.cheese_retry_core.config;

import com.sayanth_ranjith.cheese_retry_core.aspect.CheeseAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class CheeseRetryAutoConfiguration {

    @Bean
    public CheeseAspect cheeseAspect() {
        return new CheeseAspect();
    }

}
