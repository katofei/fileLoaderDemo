package com.example.demo.reports.view;

import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelView extends AbstractXlsView {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        style.setFillForegroundColor(HSSFColor.CORNFLOWER_BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);
        return style;
    }


    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        /*Font font = workbook.createFont();
        font.setBold(false);
        font.setColor(HSSFColor.BLACK.index);
        style.setFont(font);*/
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm:ss"));
        return style;
    }


    private void createWebContentHeader(Sheet sheet, CellStyle style) {
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
        header.createCell(5).setCellValue("Modified by");
        header.getCell(5).setCellStyle(style);

    }

    private void createMediaHeader(Sheet sheet, CellStyle style) {
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
        header.createCell(5).setCellValue("Extension");
        header.getCell(5).setCellStyle(style);
        header.createCell(6).setCellValue("Mime type");
        header.getCell(6).setCellStyle(style);
        header.createCell(7).setCellValue("Modified by");
        header.getCell(7).setCellStyle(style);
    }

    @Override
    public void buildExcelDocument(Map<String, Object> model, Workbook workbook,
                                   HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        if ("media".equals(model.get("xlsType"))) {
            log.info("buildExcelDocument started for  media files");
            Date today = new Date();
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "media-file-details-"
                    + dateFormat.format(today) + ".xls");

            @SuppressWarnings("unchecked")
            List<MediaFile> mediaFileList = (List<MediaFile>) model.get("mediaList");

            Sheet sheet = workbook.createSheet("Media files");
            sheet.setDefaultColumnWidth(20);

            CellStyle style = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            createMediaHeader(sheet, style);


            int rowCount = 1;
            Row mediaFileRow;

            if (mediaFileList != null) {
                for (MediaFile file : mediaFileList) {
                    mediaFileRow = sheet.createRow(rowCount++);
                    mediaFileRow.createCell(0).setCellValue(file.getName());
                    mediaFileRow.createCell(1).setCellValue(file.getVersion());
                    mediaFileRow.createCell(2).setCellValue(file.getSize());
                    mediaFileRow.createCell(3).setCellValue(file.getFolder());
                    Cell cell = mediaFileRow.createCell(4);
                    cell.setCellValue(file.getModifiedWhen());
                    cell.setCellStyle(dateStyle);
                    mediaFileRow.createCell(5).setCellValue(file.getExtension());
                    mediaFileRow.createCell(6).setCellValue(file.getMimeType());
                    mediaFileRow.createCell(7).setCellValue(file.getModifiedBy());
                }
            }
        } else if ("web content".equals(model.get("xlsType"))) {
            log.info("buildExcelDocument started for web content");
            Date today = new Date();
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "web-content-details-" +
                    dateFormat.format(today) + ".xls");

            @SuppressWarnings("unchecked")
            List<Content> contentList = (List<Content>) model.get("contentList");

            Sheet sheet = workbook.createSheet("Web content");
            sheet.setDefaultColumnWidth(20);

            CellStyle style = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            createWebContentHeader(sheet, style);

            int rowCount = 1;
            Row mediaFileRow;
            if (contentList != null) {
                for (Content content : contentList) {
                    mediaFileRow = sheet.createRow(rowCount++);
                    mediaFileRow.createCell(0).setCellValue(content.getName());
                    mediaFileRow.createCell(1).setCellValue(content.getVersion());
                    mediaFileRow.createCell(2).setCellValue(content.getSize());
                    mediaFileRow.createCell(3).setCellValue(content.getFolder());
                    Cell cell = mediaFileRow.createCell(4);
                    cell.setCellValue(content.getModifiedWhen());
                    cell.setCellStyle(dateStyle);
                    mediaFileRow.createCell(7).setCellValue(content.getModifiedBy());
                }
            }
        }
    }
}