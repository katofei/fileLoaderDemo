package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
@Slf4j
public class TableViewController{

    private final QueryExecutor queryExecutor;

    @Autowired
    public TableViewController(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @GetMapping(value = "/report")
    public ModelAndView getView(){
        return new ModelAndView("report");
}

    @PostMapping(value = "/report/view/{contentId}")
    public String chooseContent(@PathVariable("contentId") String contentId) {
        log.info("chooseContent started");
        log.info("PathVariable contentName {}", contentId);
        if (("webContent").equals(contentId)) {
            return "redirect:/report/view/webContent";
        } else if ("mediaFiles".equals(contentId)) {
            return "redirect:/report/view/mediaFiles";
        }
        return "";
    }


    @GetMapping(value = "/report/view/webContent")
    public ModelAndView viewCurrentWebContent() throws SQLException {
        log.info("viewCurrentWebContent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<Content> allContentList = queryExecutor.getAllWebContent();
        if (allContentList.isEmpty()) {
            modelAndView.addObject("message", "Error!!\n Web content list returned empty");
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
            modelAndView.addObject("message", "Error!!\n Media files list returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        return modelAndView;
    }

    @GetMapping(value = "/report/compare/mediaFile")
    public ModelAndView compareMediaWithCurrent() throws SQLException {
        log.info("compareMediaWithCurrent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        if (allMedia.isEmpty()) {
            modelAndView.addObject("message", "Error!!\n Media files list returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        return modelAndView;
    }


    @GetMapping(value = "/report/compare/webContent")
    public ModelAndView compareContentWithCurrent() throws SQLException {
        log.info("compareContentWithCurrent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        if (allMedia.isEmpty()) {
            modelAndView.addObject("message", "Error!!\n Media files list returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        return modelAndView;
    }

}
