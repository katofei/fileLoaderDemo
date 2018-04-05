package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.entity.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (!listFromFile.isEmpty())
            if (listFromFile.get(0) instanceof MediaFile) {
                List<MediaFile> mediaFileListFromServer = queryExecutor.getAllMediaFiles();
                if (mediaFileListFromServer.isEmpty()) {
                    modelAndView.addObject("errorMessage", "Error!!\n Web content list form server returned empty");
                } else {
                    compareMediaFiles(mediaFileListFromServer, listFromFile, modelAndView);
                }
            } else if (listFromFile.get(0) instanceof WebContent) {
                List<WebContent> contentListFromServer = queryExecutor.getAllWebContent();
                if (contentListFromServer.isEmpty()) {
                    modelAndView.addObject("errorMessage", "Error!!\n Web content list form server returned empty");
                } else {
                    compareWebContent(contentListFromServer, listFromFile, modelAndView);
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
        if (!(listFromFirstFile.isEmpty()) && !(listFromSecondFile.isEmpty())) {
            if (listFromFirstFile.get(0) instanceof MediaFile) {
                compareMediaFiles(listFromFirstFile, listFromSecondFile, modelAndView);
            } else if (listFromFirstFile.get(0) instanceof WebContent) {
                compareWebContent(listFromFirstFile, listFromSecondFile, modelAndView);
            }
        }
        return modelAndView;
    }

    private static boolean compareLists(List<? extends Content> firstList, List<? extends Content> secondList) {
        return firstList.stream().anyMatch(elem -> secondList.containsAll(firstList));
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

    private void compareWebContent(List<? extends Content> serverList, List<? extends Content> fileList, ModelAndView modelAndView) throws ParseException {
        if (compareLists(formatDate(serverList), fileList)) {
            modelAndView.addObject("allContentList", serverList);
            modelAndView.addObject("successMessage", "No differences found");
        } else {
            modelAndView.addObject("allContentList", joinLists(serverList, fileList));
            modelAndView.addObject("errorMessage", "Your resources are not equal");
        }
    }

    private void compareMediaFiles(List<? extends Content> serverList, List<? extends Content> fileList, ModelAndView modelAndView) throws ParseException {
        if (compareLists(formatDate(serverList), fileList)) {
            modelAndView.addObject("mediaList", serverList);
            modelAndView.addObject("successMessage", "No differences found");
        } else {
            modelAndView.addObject("mediaList", joinLists(serverList, fileList));
            modelAndView.addObject("errorMessage", "Your resources are not equal");
        }

    }

    private List<Content> joinLists(List<? extends Content> firstList, List<? extends Content> secondList) {
        ArrayList<Content> elementsFromFirst = new ArrayList<>(CollectionUtils.subtract(firstList, secondList));
        ArrayList<Content> elementsFromSecond = new ArrayList<>(CollectionUtils.subtract(secondList, firstList));
        /*secondList.retainAll(firstList);*/
//        return Stream.concat(firstList.stream(), secondList.stream()).sorted().collect(Collectors.toList());
        /*  return Stream.concat(firstList.stream(), secondList.stream()).distinct().sorted().collect(Collectors.toList());*/
        return Stream.concat(elementsFromFirst.stream(), elementsFromSecond.stream()).sorted().collect(Collectors.toList());
    }


}
