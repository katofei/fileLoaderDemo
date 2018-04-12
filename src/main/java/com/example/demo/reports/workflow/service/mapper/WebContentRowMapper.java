package com.example.demo.reports.workflow.service.mapper;

import com.example.demo.reports.workflow.model.WebContent;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class WebContentRowMapper implements  RowMapper<WebContent> {

    @Override
    public WebContent mapRow(ResultSet resultSet, int i) throws SQLException {
        WebContent content = new WebContent();
        content.setId(resultSet.getInt("id_"));
        content.setName(resultSet.getString("title_simple"));
        content.setVersion(resultSet.getDouble("version"));
        content.setFolder(resultSet.getString("folder"));
        content.setModifiedWhen(resultSet.getTimestamp("modifieddate"));
        content.setModifiedBy(resultSet.getString("modified_by"));
        content.setSize(resultSet.getLong("size_"));
        return content;
    }
}
