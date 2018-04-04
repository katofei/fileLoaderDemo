package com.example.demo.reports.entity.mapper;

import com.example.demo.reports.entity.WebContent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WebContentRowMapper implements  RowMapper<WebContent> {

    @Override
    public WebContent mapRow(ResultSet resultSet, int i) throws SQLException {
        WebContent content = new WebContent();
        content.setName(resultSet.getString("title_simple"));
        content.setVersion(resultSet.getDouble("version"));
        content.setFolder(resultSet.getString("folder"));
        content.setModifiedWhen(resultSet.getTimestamp("modifieddate"));
        content.setSize(resultSet.getLong("size_"));
        return content;
    }
}
