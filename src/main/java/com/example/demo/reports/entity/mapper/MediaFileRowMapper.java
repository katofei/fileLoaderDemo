package com.example.demo.reports.entity.mapper;

import com.example.demo.reports.QueryReader;
import com.example.demo.reports.entity.MediaFile;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MediaFileRowMapper implements QueryReader, RowMapper<MediaFile>{

    @Override
    public MediaFile mapRow(ResultSet resultSet, int i) throws SQLException {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(resultSet.getString("title"));
        mediaFile.setFolder(resultSet.getString("folder"));
        mediaFile.setVersion(resultSet.getDouble("version"));
        mediaFile.setExtension(resultSet.getString("extension"));
        mediaFile.setModifiedWhen(resultSet.getDate("modifieddate"));
        mediaFile.setMimeType(resultSet.getString("mimetype"));
       return mediaFile;
    }
}
