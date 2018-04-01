package com.example.demo.reports.view.resolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ViewResolversConfig{
    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setContentNegotiationManager(manager);

        // Define all possible view resolvers
        List<ViewResolver> resolvers = new ArrayList<>();

        InternalResourceViewResolver mainResolver = new InternalResourceViewResolver();
        mainResolver.setSuffix(".html");
        resolvers.add(mainResolver);
        resolvers.add(excelViewResolver());

        resolver.setViewResolvers(resolvers);
        return mainResolver;
    }

    @Bean public ViewResolver excelViewResolver() {
        return new ExcelViewResolver();
    }
}
