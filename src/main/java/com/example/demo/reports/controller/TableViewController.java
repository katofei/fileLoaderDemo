package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class TableViewController {

    private final QueryExecutor queryExecutor;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

    @Autowired
    public TableViewController(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @GetMapping(value = "/report")
    public ModelAndView getView(@ModelAttribute("errorMessage") String error, @ModelAttribute("successMessage") String success,
                                @ModelAttribute("contentFromFileList") ArrayList<Content> contentList,
                                @ModelAttribute("mediaFromFileList") ArrayList<MediaFile> mediaFiles ) {
        ModelAndView modelAndView = new ModelAndView("report");
        modelAndView.addObject("multipartFile", null);
        modelAndView.addObject("multipartFiles", new ArrayList<>());
        return modelAndView;
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
    public ModelAndView compareContentWithCurrent(HttpSession session) throws SQLException {
        log.info("compareContentWithCurrent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<Content> contentListFromServer = queryExecutor.getAllWebContent();
        if (contentListFromServer.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Web content list form server returned empty");
        }
        List<Content> contentList = (List<Content>)session.getAttribute("contentFromFileList");
        contentList = contentList.stream().filter( content -> !content.checkForNull()).collect(Collectors.toList());
        contentList.forEach(content -> {
            try {
                content.setModifiedWhen(dateFormat.parse(dateFormat.format(content.getModifiedWhen())));
            } catch (ParseException e) {
                e.getStackTrace();
            }
        });
        if (compareLists(contentListFromServer, contentList)) {
            modelAndView.addObject("allContentList", contentListFromServer);
            modelAndView.addObject("successMessage", "File and server are identical");
        }

        return modelAndView;
    }


    @GetMapping(value = "/report/compare/mediaWithServer")
    public ModelAndView compareMediaWithCurrent(@ModelAttribute("mediaFromFileList") ArrayList<MediaFile> mediaFiles) throws SQLException {
        log.info("compareMediaWithCurrent started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> mediaFilesFromServer = queryExecutor.getAllMediaFiles();
        if (mediaFilesFromServer.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Media files list from server returned empty");
        }
        if (compareLists(mediaFiles, mediaFilesFromServer)) {
            modelAndView.addObject("successMessage", "File and server are  identical");
            modelAndView.addObject("mediaList", mediaFilesFromServer);
        }

        return modelAndView;
    }

/*   @GetMapping(value = "/report/compare/webFiles")
    public ModelAndView compareContent(@ModelAttribute("reports") List<File> file) throws SQLException {
        log.info("compareContent started");
        ModelAndView modelAndView = new ModelAndView("report");


        return modelAndView;
    }


    @GetMapping(value = "/report/compare/mediaFiles")
    public ModelAndView compareMedia(@ModelAttribute("reports") List<File> file) throws SQLException {
        log.info("compareMedia started");



        return modelAndView;
    }*/

    private static boolean compareLists(List firstList, List secondList) {
        return firstList.toString().contentEquals(secondList.toString());
    }
}
