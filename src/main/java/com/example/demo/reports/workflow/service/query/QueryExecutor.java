package com.example.demo.reports.workflow.service.query;

import com.example.demo.reports.workflow.model.MediaFile;
import com.example.demo.reports.workflow.model.WebContent;
import com.example.demo.reports.workflow.service.mapper.MediaFileRowMapper;
import com.example.demo.reports.workflow.service.mapper.WebContentRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QueryExecutor implements QueryReader {

    private static final String FILE_PATH_TO_MEDIA_SCRIPT = "src/main/resources/queries/get_media.sql";
    private static final String FILE_PATH_TO_CONTENT_SCRIPT = "src/main/resources/queries/get_web_content.sql";

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


    public List<WebContent> getAllWebContent() throws SQLException {
        log.info("getAllWebContent started");
        List<WebContent> contentList = new ArrayList<>();
        String query = readQueryFromFile(FILE_PATH_TO_CONTENT_SCRIPT);
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement pst = connection.prepareStatement(query);
            try (ResultSet rs = pst.executeQuery()) {
                int i = 0;
                while (rs.next()) {
                    WebContent content = webMapper.mapRow(rs, i++);
                    content.setResource("server");
                    contentList.add(content);
                }
            } catch (SQLException ex) {
                log.error( "PSQL Exception : {}" , ex.getMessage());
                throw new SQLException("PSQL Exception :" + ex.getMessage());
            }
        } catch (SQLException e) {
            log.error("Database connection failed");
            throw new SQLException("Database connection failed");
        }
        return contentList;

    }

    public List<MediaFile> getAllMediaFiles() throws SQLException {
        log.info("getAllMediaFiles started");
        List<MediaFile> mediaFileList = new ArrayList<>();
        String query = readQueryFromFile(FILE_PATH_TO_MEDIA_SCRIPT);
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement pst = connection.prepareStatement(query);
            try (ResultSet rs = pst.executeQuery()) {
                int i = 0;
                while (rs.next()) {
                    MediaFile mediaFile = mediaMapper.mapRow(rs, i++);
                    mediaFile.setResource("server");
                    mediaFileList.add(mediaFile);
                }
            } catch (SQLException ex) {
                log.error("PSQL Exception : {}" , ex.getMessage());
                throw new SQLException("PSQL Exception :" + ex.getMessage());
            }
        } catch (SQLException e) {
            log.error("Database connection failed");
            throw new SQLException("Database connection failed");
        }
        return mediaFileList;
    }
}
