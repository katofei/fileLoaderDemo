package com.example.demo.reports.workflow.controller;

import com.example.demo.reports.workflow.service.query.QueryExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class FileDownloadController {

    private final QueryExecutor queryExecutor;

    @Autowired
    public FileDownloadController(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @GetMapping(value = "/report/download/webContent")
    public String downloadContentInfo(Model model) throws Exception {
        log.info("downloadContentInfo started");
        model.addAttribute("contentList", queryExecutor.getAllWebContent());
        model.addAttribute("xlsType", "web content");
        return "excelViewComposer";
    }

    @GetMapping(value = "/report/download/mediaFiles")
    public String downloadMediaInfo(Model model) throws Exception {
        log.info("downloadMediaInfo started");
        model.addAttribute("mediaList", queryExecutor.getAllMediaFiles());
        model.addAttribute("xlsType", "media");
        return "excelViewComposer";
    }

}

