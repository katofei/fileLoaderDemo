package com.example.demo.reports.workflow.service.mapper;

import com.example.demo.reports.workflow.model.MediaFile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class MediaFileRowMapper implements RowMapper<MediaFile>{


    @Override
    public MediaFile mapRow(ResultSet resultSet, int i) throws SQLException {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(resultSet.getInt("fileentryid"));
        mediaFile.setName(resultSet.getString("title"));
        mediaFile.setPath(resultSet.getString("full_path"));
        mediaFile.setVersion(resultSet.getDouble("version"));
        mediaFile.setExtension(resultSet.getString("extension"));
        mediaFile.setSize(resultSet.getLong("size_"));
        mediaFile.setModifiedWhen(resultSet.getTimestamp("modifieddate"));
        mediaFile.setModifiedBy(resultSet.getString("modified_by"));
        mediaFile.setMimeType(resultSet.getString("mimetype"));
        return mediaFile;
    }
}

