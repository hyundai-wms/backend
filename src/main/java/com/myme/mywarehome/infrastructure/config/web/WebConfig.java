package com.myme.mywarehome.infrastructure.config.web;

import com.myme.mywarehome.infrastructure.config.resolver.SelectedDateArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SelectedDateArgumentResolver selectedDateArgumentResolver;

    public WebConfig(SelectedDateArgumentResolver selectedDateArgumentResolver) {
        this.selectedDateArgumentResolver = selectedDateArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(selectedDateArgumentResolver);
    }
}
