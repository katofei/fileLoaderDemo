package com.example.demo.reports.workflow.service.file.parser;

import com.example.demo.reports.workflow.model.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.javafunk.excelparser.SheetParser;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebContentFileParser {

    public List<WebContent> parse(String fileName) throws IOException {
       log.info("parse for content started");
        List<WebContent> contentList;
        try {
            FileInputStream fis = new FileInputStream(fileName);

            Workbook workbook = null;
            if (fileName.toLowerCase().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (fileName.toLowerCase().endsWith("xls")) {
                workbook = new HSSFWorkbook(fis);
            }

            Sheet sheet = workbook != null ? workbook.getSheetAt(0) : null;
            SheetParser parser = new SheetParser();
            List<WebContent> sectionList = parser.createEntity(sheet, WebContent.class, error -> {
                throw error;
            });
            sectionList = new ArrayList<>(sectionList);
            contentList = sectionList.stream().filter(content -> !content.checkForNull()).collect(Collectors.toList());

        } catch (IOException e) {
            throw new IOException("Error during parsing file");
        }
        return contentList;
    }
}
