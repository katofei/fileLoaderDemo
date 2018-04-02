package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.file.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class FileLoaderController {

    private final QueryExecutor queryExecutor;
       @Autowired
    public FileLoaderController(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;}

    @GetMapping(value = "/report/download/webContent")
    public String downloadContentInfo(Model model)
            throws Exception {
        log.info("downloadContentInfo started");
        model.addAttribute("contentList", queryExecutor.getAllWebContent());
        model.addAttribute("xlsType", "web content");
        return "";
    }

    @GetMapping(value = "/report/download/mediaFiles")
    public String downloadMediaInfo(Model model)
            throws Exception {
        log.info("downloadMediaInfo started");
        model.addAttribute("mediaList", queryExecutor.getAllMediaFiles());
        model.addAttribute("xlsType", "media");
        return "";
    }

    @PostMapping(value = "/report/loadSingleFile")
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        log.info("singleFileUpload started");
        ModelAndView modelAndView = new ModelAndView("report");
        if (file.isEmpty()) {
            modelAndView.addObject("errorMessage", "Selected file is empty. Choose another one, please.");
            return modelAndView;
        }
        FileHelper.upload(file,request, "xlsReports");
        modelAndView.addObject("successMessage", "File " + file.getName() + "successfully uploaded");
        return modelAndView;
    }

    @PostMapping(value = "/report/loadMultiFiles")
    public ModelAndView multiFileUpload(@RequestParam("files") MultipartFile[] files, HttpServletRequest request){
        log.info("multiFileUpload started");
        ModelAndView modelAndView = new ModelAndView("report");
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                modelAndView.addObject("errorMessage", "File "+ file.getName()+ "is empty. Choose another one, please.");
                return modelAndView;
            }
            FileHelper.upload(file, request, "xlsReports");
            modelAndView.addObject("successMessage", "File " + file.getName() + "successfully uploaded");
        }
        return modelAndView;
    }

}
