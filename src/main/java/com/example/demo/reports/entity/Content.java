package com.example.demo.reports.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Content {

    private String name;
    private double version;
    private long size;
    private String folder;
    private Date modifiedWhen;

}
