package com.example.demo.reports.workflow.service.query;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public interface QueryReader {

    default String readQueryFromFile(final String filePath) throws IOException {
        String query;
        try{
            Path path = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
            byte[] fileBytes = Files.readAllBytes(path);
            query = new String(fileBytes);
        }
        catch (URISyntaxException | IOException ex) {
            throw new IOException("Error during file reading :" + ex.getMessage());
        }
        return query;
    }
}
