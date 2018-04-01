package com.example.demo.reports.view;

import com.example.demo.reports.entity.MediaFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
public class MediaFileExcelView extends AbstractXlsView implements ExcelView {

    public MediaFileExcelView(){
        setContentType("application/xls");
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
        header.createCell(5).setCellValue("Extension");
        header.getCell(5).setCellStyle(style);
        header.createCell(6).setCellValue("Mime type");
        header.getCell(6).setCellStyle(style);
    }

    @Override
    public void buildExcelDocument(Map<String, Object> model, Workbook workbook,
                                      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        log.info("buildExcelDocument started for  media files");
        Date today = new Date();
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "media-file-details-"
                +dateFormat.format(today)+ ".xls");

        @SuppressWarnings("unchecked")
        List<MediaFile> mediaFileList = (List<MediaFile>) model.get("mediaList");

        Sheet sheet = workbook.createSheet("Media files");
        sheet.setDefaultColumnWidth(30);

        CellStyle style = createStyle(workbook);
        createHeader(sheet, style);

        int rowCount = 1;
        Row mediaFileRow;

        for (MediaFile file : mediaFileList) {
            mediaFileRow = sheet.createRow(rowCount++);
            mediaFileRow.createCell(0).setCellValue(file.getName());
            mediaFileRow.createCell(1).setCellValue(file.getVersion());
            mediaFileRow.createCell(2).setCellValue(file.getSize());
            mediaFileRow.createCell(3).setCellValue(file.getFolder());
            mediaFileRow.createCell(4).setCellValue(file.getModifiedWhen());
            mediaFileRow.createCell(5).setCellValue(file.getExtension());
            mediaFileRow.createCell(6).setCellValue(file.getMimeType());
        }
    }
}
