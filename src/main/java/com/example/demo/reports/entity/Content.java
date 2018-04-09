package com.example.demo.reports.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
public abstract class Content {

    private String name;
    private Double version;
    private Long size;
    private String folder;
    private Date modifiedWhen;
    private String modifiedBy;
    private String resource;

    public boolean checkForNull() {
        return (getName() == null && getFolder() == null && getModifiedWhen() == null && getSize() == null && getVersion() == null && modifiedBy== null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(name, content.name) &&
                Objects.equals(version, content.version) &&
                Objects.equals(size, content.size) &&
                Objects.equals(folder, content.folder) &&
                Objects.equals(modifiedWhen, content.modifiedWhen) &&
                Objects.equals(modifiedBy, content.modifiedBy)&&
                Objects.equals(resource, content.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, size, folder, modifiedWhen, modifiedBy, resource);
    }
}
