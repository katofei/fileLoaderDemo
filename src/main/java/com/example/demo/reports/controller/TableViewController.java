package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.entity.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class TableViewController {

    private final QueryExecutor queryExecutor;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

    @Autowired
    public TableViewController(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @GetMapping(value = "/report")
    public ModelAndView getView(@ModelAttribute("errorMessage") String error, @ModelAttribute("successMessage") String success,
                                @ModelAttribute("compareWhat") ArrayList<Content> compareWhat,
                                @ModelAttribute("compareWith") ArrayList<Content> compareWith) {
        ModelAndView modelAndView = new ModelAndView("report");
        modelAndView.addObject("multipartFile", null);
        modelAndView.addObject("multipartFiles", new ArrayList<>());
        return modelAndView;
    }

    @GetMapping(value = "/report/view/webContent")
    public ModelAndView viewCurrentWebContent() throws SQLException {
        log.info("viewCurrentWebContent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<WebContent> allContentList = queryExecutor.getAllWebContent();
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


    @GetMapping(value = "/report/compare/withServer")
    public ModelAndView compareFileWithServer(HttpSession session) throws SQLException, ParseException {
        log.info("compareFileWithServer started");
        ModelAndView modelAndView = new ModelAndView("report");
        Object compareWhat = session.getAttribute("compareWhat");
        @SuppressWarnings("unchecked")
        List<Content> listFromFile = (List<Content>) compareWhat;
        if (! listFromFile.isEmpty() && listFromFile != null)
            if (listFromFile.get(0) instanceof MediaFile) {
                List<MediaFile> mediaFileListFromServer = queryExecutor.getAllMediaFiles();
                if (mediaFileListFromServer.isEmpty()) {
                    modelAndView.addObject("errorMessage", "Error!!\n Web content list form server returned empty");
                } else {
                    List<? extends Content> mediaListFromFile = trimAndFormatCells(listFromFile);
                    compareMediaFiles(mediaFileListFromServer, mediaListFromFile, modelAndView);
                }
            } else if (listFromFile.get(0) instanceof WebContent) {
                List<WebContent> contentListFromServer = queryExecutor.getAllWebContent();
                if (contentListFromServer.isEmpty()) {
                    modelAndView.addObject("errorMessage", "Error!!\n Web content list form server returned empty");
                } else {
                    List<? extends Content> contentListFormFile = trimAndFormatCells(listFromFile);
                    compareWebContent(contentListFromServer, contentListFormFile, modelAndView);
                }
            }
        return modelAndView;
    }


    @GetMapping(value = "/report/compare/files")
    public ModelAndView compareFiles(HttpSession session) throws ParseException {
        log.info("compareFiles started");
        ModelAndView modelAndView = new ModelAndView("report");
        Object compareWhat = session.getAttribute("compareWhat");
        Object compareWith = session.getAttribute("compareWith");
        @SuppressWarnings("unchecked")
        List<Content> listFromFirstFile = (List<Content>) compareWhat;
        @SuppressWarnings("unchecked")
        List<Content> listFromSecondFile = (List<Content>) compareWith;
        if(!(listFromFirstFile.isEmpty() && listFromFirstFile != null)
                && !(listFromSecondFile.isEmpty() && listFromSecondFile != null)) {
            if (listFromFirstFile.get(0) instanceof MediaFile) {
                List<? extends Content> trimmedFirstFile = trimAndFormatCells(listFromFirstFile);
                List<? extends Content> trimmedSecondFile =trimAndFormatCells(listFromFirstFile);
                compareMediaFiles(trimmedFirstFile, trimmedSecondFile, modelAndView);
            }
            else if (listFromFirstFile.get(0) instanceof WebContent) {
                List<? extends Content> trimmedFirstFile = trimAndFormatCells(listFromFirstFile);
                List<? extends Content> trimmedSecondFile =trimAndFormatCells(listFromFirstFile);
                compareWebContent(trimmedFirstFile, trimmedSecondFile, modelAndView);
            }
        }
        return modelAndView;
    }


    private static boolean compareLists(List<? extends Content> firstList, List<? extends Content> secondList) {
        boolean result = false;
        for (Content content : firstList) {
            for (Content content1 : secondList) {
                if (content.equals(content1))
                    result = true;
            }
        }
        return result;
    }

    private List<? extends Content> trimAndFormatCells(List<? extends Content> sourceList) {
        sourceList = sourceList.stream().filter(content -> !content.checkForNull()).collect(Collectors.toList());
        for (Content content : sourceList) {
            formatCells(content);
        }
        return sourceList;
    }


    private List<? extends Content> formatDate(List<? extends Content> sourceList) throws ParseException {
        for (Content content : sourceList) {
            Date modifiedWhen = content.getModifiedWhen();
            String formattedDate = dateFormat.format(modifiedWhen);
            Date parseDate = dateFormat.parse(formattedDate);
            content.setModifiedWhen(parseDate);
        }
        return sourceList;
    }

    private void formatCells(Content content) {
        if ("".equals(content.getFolder())) {
            content.setFolder(null);
        }
        if ("".equals(content.getName())) {
            content.setName(null);
        }
    }

    private void compareWebContent(List<? extends Content> serverList, List<? extends Content> fileList, ModelAndView modelAndView) throws ParseException {
        if (compareLists(formatDate(serverList), fileList)) {
            modelAndView.addObject("allContentList", serverList);
            modelAndView.addObject("successMessage", "No differences found");
        } else {
            modelAndView.addObject("webContentFromFile", fileList);
            modelAndView.addObject("allContentList", serverList);
            modelAndView.addObject("errorMessage", "Your resources are not equal");
        }
    }

    private void compareMediaFiles(List<? extends Content> serverList, List<? extends Content> fileList, ModelAndView modelAndView) throws ParseException {
        if (compareLists(formatDate(serverList), fileList)) {
            modelAndView.addObject("mediaList", serverList);
            modelAndView.addObject("successMessage", "No differences found");
        } else {
            modelAndView.addObject("mediaList", serverList);
            modelAndView.addObject("mediaListFromFile", fileList);
            modelAndView.addObject("errorMessage", "Your resources are not equal");
        }
    }
}
