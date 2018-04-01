package com.example.demo.reports.view;

import com.example.demo.reports.entity.Content;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WebContentExcelView  extends AbstractXlsView  implements ExcelView {

    public WebContentExcelView() {
       setContentType("application/xsl");
    }

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

    private void createHeader(Sheet sheet, CellStyle style) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Title");
        header.getCell(0).setCellStyle(style);
        header.createCell(1).setCellValue("Version");
        header.getCell(1).setCellStyle(style);
        header.createCell(2).setCellValue("Size");
        header.getCell(2).setCellStyle(style);
        header.createCell(3).setCellValue("Folder");
        header.getCell(3).setCellStyle(style);
        header.createCell(4).setCellValue("Modified when");
        header.getCell(4).setCellStyle(style);
    }

    @Override
    public void buildExcelDocument(Map<String, Object> model, Workbook workbook,
                                      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        log.info("buildExcelDocument started for web content");
        Date today = new Date();
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "web-content-details-"+
                dateFormat.format(today)+ ".xls");

        @SuppressWarnings("unchecked")
        List<Content> contentList = (List<Content>) model.get("contentList");

        Sheet sheet = workbook.createSheet("Web content");
        sheet.setDefaultColumnWidth(30);

        CellStyle style = createStyle(workbook);
        createHeader(sheet, style);

        int rowCount = 1;
        Row mediaFileRow;

        for (Content content : contentList) {
            mediaFileRow = sheet.createRow(rowCount++);
            mediaFileRow.createCell(0).setCellValue(content.getName());
            mediaFileRow.createCell(1).setCellValue(content.getVersion());
            mediaFileRow.createCell(2).setCellValue(content.getSize());
            mediaFileRow.createCell(3).setCellValue(content.getFolder());
            mediaFileRow.createCell(4).setCellValue(content.getModifiedWhen());
        }
    }
}
