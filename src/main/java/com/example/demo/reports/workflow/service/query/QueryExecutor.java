package com.example.demo.reports.workflow.service.query;

import com.example.demo.reports.workflow.model.MediaFile;
import com.example.demo.reports.workflow.model.WebContent;
import com.example.demo.reports.workflow.service.mapper.MediaFileRowMapper;
import com.example.demo.reports.workflow.service.mapper.WebContentRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QueryExecutor implements QueryReader {

    private static final String FILE_PATH_TO_MEDIA_SCRIPT = "/queries/get_media.sql";
    private static final String FILE_PATH_TO_CONTENT_SCRIPT = "/queries/get_web_content.sql";

    private final MediaFileRowMapper mediaMapper;
    private final WebContentRowMapper webMapper;

    @Autowired
    public QueryExecutor(MediaFileRowMapper mediaMapper, WebContentRowMapper webMapper) {
        this.mediaMapper = mediaMapper;
        this.webMapper = webMapper;
    }

    private static final String url = System.getenv("DB_URL");
    private static final String user = System.getenv("DB_USER");
    private static final String password = System.getenv("DB_PASSWORD");


    public List<WebContent> getAllWebContent() throws SQLException, IOException {
        log.info("getAllWebContent started");
        List<WebContent> contentResultList;
        String query = readQueryFromFile(FILE_PATH_TO_CONTENT_SCRIPT);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            contentResultList = getContentResultSet(connection, query);
        } catch (SQLException e) {
            log.error("Database connection failed");
            throw new SQLException("Database connection failed");
        }
        return contentResultList;
    }

    private List<WebContent> getContentResultSet(Connection connection, String query) throws SQLException {
        List<WebContent> contentList = new ArrayList<>();
        PreparedStatement pst = connection.prepareStatement(query);
        try (ResultSet rs = pst.executeQuery()) {
            int i = 0;
            while (rs.next()) {
                WebContent content = webMapper.mapRow(rs, i++);
                content.setResource("server");
                contentList.add(content);
            }
        } catch (SQLException ex) {
            log.error("PSQL Exception : {}", ex.getMessage());
            throw new SQLException("PSQL Exception :" + ex.getMessage());
        }
        return contentList;
    }


    public List<MediaFile> getAllMediaFiles() throws SQLException, IOException {
        log.info("getAllMediaFiles started");
        List<MediaFile> mediaFileResultList;
        String query = readQueryFromFile(FILE_PATH_TO_MEDIA_SCRIPT);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            mediaFileResultList = getMediaResultSet(connection, query);
        } catch (SQLException e) {
            log.error("Database connection failed");
            throw new SQLException("Database connection failed");
        }
        return mediaFileResultList;
    }

    private List<MediaFile> getMediaResultSet(Connection connection, String query) throws SQLException {
        List<MediaFile> mediaList = new ArrayList<>();
        PreparedStatement pst = connection.prepareStatement(query);
        try (ResultSet rs = pst.executeQuery()) {
            int i = 0;
            while (rs.next()) {
                MediaFile mediaFile = mediaMapper.mapRow(rs, i++);
                mediaFile.setResource("server");
                mediaList.add(mediaFile);
            }
        } catch (SQLException ex) {
            log.error("PSQL Exception : {}", ex.getMessage());
            throw new SQLException("PSQL Exception :" + ex.getMessage());
        }
        return mediaList;
    }
}
