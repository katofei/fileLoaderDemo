package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.file.ContentFileParser;
import com.example.demo.reports.file.MediaFileParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@Slf4j
public class TableViewController {

    private final QueryExecutor queryExecutor;
    private final ContentFileParser contentFileParser;
    private final MediaFileParser mediaFileParser;

    @Autowired
    public TableViewController(QueryExecutor queryExecutor, ContentFileParser contentFileParser, MediaFileParser mediaFileParser) {
        this.queryExecutor = queryExecutor;
        this.contentFileParser = contentFileParser;
        this.mediaFileParser = mediaFileParser;
    }

    @GetMapping(value = "/report")
    public ModelAndView getView() {
        return new ModelAndView("report");
    }

    @GetMapping(value = "/report/view/webContent")
    public ModelAndView viewCurrentWebContent() throws SQLException {
        log.info("viewCurrentWebContent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<Content> allContentList = queryExecutor.getAllWebContent();
        if (allContentList.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Web content list returned empty");
        }
        modelAndView.addObject("allContentList", allContentList);

        return modelAndView;
    }

    @GetMapping(value = "/report/view/mediaFiles")
    public ModelAndView viewCurrentMediaFiles() throws SQLException {
        log.info("viewCurrentMediaFiles started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        if (allMedia.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Media files list returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        return modelAndView;
    }


   @GetMapping(value = "/report/compare/webWithServer")
    public ModelAndView compareContentWithCurrent(@RequestParam("file") File file) throws SQLException, IOException {
        log.info("compareContentWithCurrent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<Content> allContentList = queryExecutor.getAllWebContent();
        if (allContentList.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Web content list form server returned empty");
        }
        modelAndView.addObject("allContentList", allContentList);
        List<Content> contentFromFileList = initializeContentSheet(file);
        modelAndView.addObject("contentFromFileList", contentFromFileList);
        return modelAndView;
    }


    @GetMapping(value = "/report/compare/mediaWithServer")
    public ModelAndView compareMediaWithCurrent(@RequestParam("file") File file) throws SQLException, IOException {
        log.info("compareMediaWithCurrent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        if (allMedia.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Media files list from server returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        List<MediaFile> mediaFromFileList = initializeMediaSheet(file);
        modelAndView.addObject("mediaFromFileList", mediaFromFileList);
        return modelAndView;
    }

/*    @GetMapping(value = "/report/compare/webFiles")
    public ModelAndView compareContent() throws SQLException {
        log.info("compareContent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<Content> allContentList = queryExecutor.getAllWebContent();
        if (allContentList.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Web content list returned empty");
        }
        modelAndView.addObject("allContentList", allContentList);
        return modelAndView;
    }


    @GetMapping(value = "/report/compare/mediaFiles")
    public ModelAndView compareMedia() throws SQLException {
        log.info("compareMedia started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        if (allMedia.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Media files list returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        return modelAndView;
    }*/


    private List<Content> initializeContentSheet(File file) throws IOException {
        return contentFileParser.parse(file.getAbsolutePath());
    }

    private List<MediaFile> initializeMediaSheet(File file) throws IOException {
        return mediaFileParser.parse(file.getAbsolutePath());
    }


}
