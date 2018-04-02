package com.example.demo.reports.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javafunk.excelparser.annotations.ExcelField;
import org.javafunk.excelparser.annotations.ExcelObject;
import org.javafunk.excelparser.annotations.ParseType;

import java.util.Date;

@Data
@NoArgsConstructor
@ExcelObject(parseType = ParseType.ROW, start = 2, end = 500)
public class MediaFile {
    @ExcelField(position = 1)
    private String name;
    @ExcelField(position = 2)
    private double version;
    @ExcelField(position = 3)
    private long size;
    @ExcelField(position = 4)
    private String folder;
    @ExcelField(position = 5)
    private Date modifiedWhen;
    @ExcelField(position = 6)
    private String extension;
    @ExcelField(position = 7)
    private String mimeType;
}