package com.example.demo.reports;

import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.entity.mapper.ContentRowMapper;
import com.example.demo.reports.entity.mapper.MediaFileRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QueryExecutor implements QueryReader {

    private static final String FILE_PATH_TO_MEDIA_SCRIPT = "src/main/resources/queries/get_media.sql";
    private static final String FILE_PATH_TO_CONTENT_SCRIPT = "src/main/resources/queries/get_web_content.sql";

    private static final String url = System.getenv("DB_URL");
    private static final String user = System.getenv("DB_USER");
    private static final String password = System.getenv("DB_PASSWORD");


    public List<Content> getAllWebContent() throws SQLException {
        log.info("getAllWebContent started");
        List<Content> contentList = new ArrayList<>();
        String query = readQueryFromFile(FILE_PATH_TO_CONTENT_SCRIPT);
        log.info(query);
        try(Connection con = DriverManager.getConnection(url, user, password)){
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            ContentRowMapper mapper = new ContentRowMapper();
            int i = 0;
            while (rs.next()){
                Content content = mapper.mapRow(rs, i++);
                contentList.add(content);
            }
        } catch (SQLException e) {
            log.error("Error during data base connection or query execution");
            throw new SQLException("Error during data base connection or query execution");
        }
        return contentList;

    }

    public List<MediaFile> getAllMediaFiles() throws SQLException {
        log.info("getAllMediaFiles started");
        List<MediaFile> mediaFileList = new ArrayList<>();
        String query = readQueryFromFile(FILE_PATH_TO_MEDIA_SCRIPT);
        log.info(query);
        try(Connection con = DriverManager.getConnection(url, user, password)){
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            MediaFileRowMapper mapper = new MediaFileRowMapper();
            int i = 0;
            while (rs.next()){
                MediaFile mediaFile = mapper.mapRow(rs, i++);
                mediaFileList.add(mediaFile);
            }
        } catch (SQLException e) {
            log.error("Error during data base connection or query execution");
            throw new SQLException("Error during data base connection or query execution");
        }
        return mediaFileList;
    }
}
