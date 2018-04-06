package com.example.demo.reports.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.javafunk.excelparser.annotations.ExcelField;
import org.javafunk.excelparser.annotations.ExcelObject;
import org.javafunk.excelparser.annotations.ParseType;

import java.util.Comparator;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ExcelObject(parseType = ParseType.ROW, start = 2, end = 500)
public class WebContent extends Content implements Comparable<WebContent>, Comparator<WebContent> {

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
    @ExcelField(position = 6)
    private String modifiedBy;

    public boolean checkForNull() {
      return   super.checkForNull();
    }

    @Override
    public int compareTo(WebContent webContent) {
        return this.name.compareTo(webContent.name);
    }

    @Override
    public int compare(WebContent webContent, WebContent t1) {
        if(webContent.size.equals(t1.size)){
            return 0;
        }
        else if(webContent.size>(t1.size)){
            return 1;
        }
        else return -1;
    }
}
