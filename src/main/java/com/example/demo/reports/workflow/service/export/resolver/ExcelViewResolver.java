package com.example.demo.reports.workflow.service.export.resolver;

import com.example.demo.reports.workflow.service.export.ExcelViewComposer;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

public class ExcelViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String s, Locale locale) {
        return new ExcelViewComposer();
    }

}
