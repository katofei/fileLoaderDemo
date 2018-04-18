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
public class MediaFile extends Content implements Comparable<MediaFile>, Comparator<MediaFile> {

    @ExcelField(position = 1)
    private Integer id;
    @ExcelField(position = 2)
    private String name;
    @ExcelField(position = 3)
    private Double version;
    @ExcelField(position = 4)
    private Long size;
    @ExcelField(position = 5)
    private String path;
    @ExcelField(position = 6)
    private Date modifiedWhen;
    @ExcelField(position = 7)
    private String extension;
    @ExcelField(position = 8)
    private String mimeType;
    @ExcelField(position = 9)
    private String modifiedBy;

    private String resource;

    @Override
    public final boolean checkForNull() {
        return (getName() == null && getPath() == null && getModifiedWhen() == null
                && getSize() == null && getMimeType() == null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFile mediaFile = (MediaFile) o;
        return Objects.equals(id, mediaFile.id) &&
                Objects.equals(name, mediaFile.name)&&
                Objects.equals(version, mediaFile.version) &&
                Objects.equals(size, mediaFile.size) &&
                Objects.equals(path, mediaFile.path) &&
                Objects.equals(mimeType, mediaFile.mimeType) &&
                Objects.equals(extension, mediaFile.extension) &&
                Objects.equals(modifiedWhen, mediaFile.modifiedWhen) &&
                Objects.equals(modifiedBy, mediaFile.modifiedBy);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, name, version, size, path, mimeType, extension, modifiedBy, modifiedWhen);
    }

    @Override
    public final int compareTo(MediaFile mediaFile) {
        return this.path.compareTo(mediaFile.path);
    }

    @Override
    public final int compare(MediaFile mediaFile, MediaFile t1) {
        if (mediaFile.size.equals(t1.size)) {
            return 0;
        } else if (mediaFile.size > (t1.size)) {
            return 1;
        } else return -1;
    }
}