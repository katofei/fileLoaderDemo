package com.example.demo.reports.workflow.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.javafunk.excelparser.annotations.ExcelField;
import org.javafunk.excelparser.annotations.ExcelObject;
import org.javafunk.excelparser.annotations.ParseType;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
@ExcelObject(parseType = ParseType.ROW, start = 2, end = 500)
public class WebContent extends Content implements Comparable<WebContent>, Comparator<WebContent> {

    @ExcelField(position = 1)
    private Integer id;

    @ExcelField(position = 2)
    private String name;

    @ExcelField(position = 3)
    private Double version;

    @ExcelField(position = 4)
    private Long size;

    @ExcelField(position = 5)
    private String folder;

    @ExcelField(position = 6)
    private Date modifiedWhen;

    @ExcelField(position = 7)
    private String modifiedBy;

    private String resource;

    @Override
    public final boolean checkForNull() {
        return getName() == null && getFolder() == null && getModifiedWhen() == null && getSize() == null
                && getVersion() == null && modifiedBy== null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebContent content = (WebContent) o;
        return Objects.equals(id, content.id) &&
                Objects.equals(name, content.name)&&
                Objects.equals(version, content.version) &&
                Objects.equals(size, content.size) &&
                Objects.equals(folder, content.folder) &&
                Objects.equals(modifiedWhen, content.modifiedWhen) &&
                Objects.equals(modifiedBy, content.modifiedBy);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, name, version, size, folder, modifiedBy, modifiedWhen);
    }

    @Override
    public final int compareTo(WebContent webContent) {
        return this.folder.compareTo(webContent.folder);
    }

    @Override
    public final int compare(WebContent webContent, WebContent t1) {
        if (webContent.size.equals(t1.size)) {
            return 0;
        } else if (webContent.size > (t1.size)) {
            return 1;
        } else return -1;
    }
}
