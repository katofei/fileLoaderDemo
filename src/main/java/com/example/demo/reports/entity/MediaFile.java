package com.example.demo.reports.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.javafunk.excelparser.annotations.ExcelField;
import org.javafunk.excelparser.annotations.ExcelObject;
import org.javafunk.excelparser.annotations.ParseType;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
@ExcelObject(parseType = ParseType.ROW, start = 2, end = 500)
@EqualsAndHashCode(callSuper = false)
public class MediaFile extends Content implements Comparable<MediaFile>, Comparator<MediaFile> {

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
    private String extension;
    @ExcelField(position = 7)
    private String mimeType;
    @ExcelField(position = 8)
    private String modifiedBy;

    @Override
    public boolean checkForNull() {
        return (getName() == null && getFolder() == null && getModifiedWhen() == null && getSize() == null && getMimeType() == null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MediaFile mediaFile = (MediaFile) o;
        return Objects.equals(name, mediaFile.name) &&
                Objects.equals(version, mediaFile.version) &&
                Objects.equals(size, mediaFile.size) &&
                Objects.equals(folder, mediaFile.folder) &&
                Objects.equals(modifiedWhen, mediaFile.modifiedWhen) &&
                Objects.equals(extension, mediaFile.extension) &&
                Objects.equals(mimeType, mediaFile.mimeType)&&
                Objects.equals(modifiedBy, mediaFile.modifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, version, size, folder, modifiedWhen, extension, mimeType, modifiedBy);
    }

    @Override
    public int compareTo(MediaFile mediaFile) {
        return this.name.compareTo(mediaFile.name);
    }

    @Override
    public int compare(MediaFile mediaFile, MediaFile t1) {
       if(mediaFile.size.equals(t1.size)){
           return 0;
       }
       else if(mediaFile.size>(t1.size)){
           return 1;
       }
       else return -1;
    }
}