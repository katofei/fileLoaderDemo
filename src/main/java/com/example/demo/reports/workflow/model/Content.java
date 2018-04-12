package com.example.demo.reports.workflow.model;

import java.util.Date;

public abstract class Content {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Double getVersion();

    public abstract void setVersion(Double version);

    public abstract Long getSize();

    public abstract void setSize(Long size);

    public abstract String getFolder();

    public abstract void setFolder(String folder) ;

    public abstract Date getModifiedWhen() ;

    public abstract void setModifiedWhen(Date modifiedWhen);

    public abstract String getModifiedBy() ;

    public abstract void setModifiedBy(String modifiedBy);

    public abstract String getResource();

    public abstract void setResource(String resource);

    public abstract boolean checkForNull();
}
