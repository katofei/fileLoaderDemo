package com.example.demo.reports.workflow.service.query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface QueryReader {

    default String readQueryFromFile(String filePath) {
        String query = "";
        try {
            query = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query;
    }
}
