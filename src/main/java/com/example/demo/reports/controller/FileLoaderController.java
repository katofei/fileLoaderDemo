package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.file.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Controller
@Slf4j
public class FileLoaderController {

    private final QueryExecutor queryExecutor;

    @Autowired
    public FileLoaderController(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

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
    public String singleFileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        log.info("singleFileUpload started");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("multipartFile");
        if (file.isEmpty()) {
          redirectAttributes.addFlashAttribute("errorMessage", "File " + file.getOriginalFilename() + " is empty. Choose another one, please.");
          return ("redirect:/report");
        }
        File report = FileHelper.upload(file, request, "xlsReports");
        redirectAttributes.addFlashAttribute("successMessage", "File " + file.getOriginalFilename() + " successfully uploaded");
        redirectAttributes.addFlashAttribute("report", report);
        return ("redirect:/report");
    }

    @PostMapping(value = "/report/loadMultiFiles")
    public String multiFileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        log.info("multiFileUpload started");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles("files");
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "File " + file.getOriginalFilename() + " is empty. Choose another one, please.");
                return ("redirect:/report");
            } else {
                FileHelper.upload(file, request, "xlsReports");
            }
            redirectAttributes.addFlashAttribute("successMessage", "Files successfully uploaded");
        }
        return ("redirect:/report");
    }

}
