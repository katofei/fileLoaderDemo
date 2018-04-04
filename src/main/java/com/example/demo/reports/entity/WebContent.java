package com.example.demo.reports.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.javafunk.excelparser.annotations.ExcelField;
import org.javafunk.excelparser.annotations.ExcelObject;
import org.javafunk.excelparser.annotations.ParseType;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ExcelObject(parseType = ParseType.ROW, start = 2, end = 500)
public class WebContent extends Content {

    @ExcelField(position = 1)
    private String name;
    @ExcelField(position = 2)
    private Double version;
    @ExcelField(position = 3)
    private Long size;
    @ExcelField(position = 4)
    private String folder;
    @ExcelField(position = 5)
    private Date modifiedWhen;

    public boolean checkForNull() {
      return   super.checkForNull();
    }
}
