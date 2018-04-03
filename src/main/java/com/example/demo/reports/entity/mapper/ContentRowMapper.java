package com.example.demo.reports.entity.mapper;

import com.example.demo.reports.entity.Content;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentRowMapper implements  RowMapper<Content> {

    @Override
    public Content mapRow(ResultSet resultSet, int i) throws SQLException {
        Content content = new Content();
        content.setName(resultSet.getString("title_simple"));
        content.setVersion(resultSet.getDouble("version"));
        content.setFolder(resultSet.getString("folder"));
        content.setModifiedWhen(resultSet.getTimestamp("modifieddate"));
        content.setSize(resultSet.getLong("size_"));
        return content;
    }
}
