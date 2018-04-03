package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.file.ContentFileParser;
import com.example.demo.reports.file.FileHelper;
import com.example.demo.reports.file.MediaFileParser;
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
import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class FileLoaderController {

    private final QueryExecutor queryExecutor;
    private final ContentFileParser contentFileParser;
    private final MediaFileParser mediaFileParser;

    @Autowired
    public FileLoaderController(QueryExecutor queryExecutor, ContentFileParser contentFileParser, MediaFileParser mediaFileParser) {
        this.queryExecutor = queryExecutor;
        this.contentFileParser = contentFileParser;
        this.mediaFileParser = mediaFileParser;
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
        String originalFilename = file.getOriginalFilename();
        log.info("Original file name {}" , originalFilename);
        if(originalFilename.contains("web-content-details")){
            File report = FileHelper.upload(file, request, "xlsReports");
            redirectAttributes.addFlashAttribute("successMessage", "File " + originalFilename + " successfully uploaded");
            List<Content> contentFromFileList = initializeContentSheet(report);
            request.getSession().setAttribute("contentFromFileList", contentFromFileList);
        }
        else if(originalFilename.contains("media-file-details")){
            File report = FileHelper.upload(file, request, "xlsReports");
            redirectAttributes.addFlashAttribute("successMessage", "File " + originalFilename + " successfully uploaded");
            List<MediaFile> mediaFromFileList = initializeMediaSheet(report);
            request.getSession().setAttribute("mediaFromFileList", mediaFromFileList);
        }
        else {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enable to parse  " + file.getOriginalFilename() + " .Please, provide correct name");
            return ("redirect:/report");
        }

        return ("redirect:/report");
    }

   /* @PostMapping(value = "/report/loadMultiFiles")
    public String multiFileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        log.info("multiFileUpload started");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles("multipartFiles");
        List<File> reportList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "File " + file.getOriginalFilename() + " is empty. Choose another one, please.");
                return ("redirect:/report");
            } else {
                File report = FileHelper.upload(file, request, "xlsReports");
                reportList.add(report);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Files successfully uploaded");

        }
        return ("redirect:/report");
    }*/


    private List<Content> initializeContentSheet(File file) throws IOException {
        return contentFileParser.parse(file.getAbsolutePath());
    }

    private List<MediaFile> initializeMediaSheet(File file) throws IOException {
        return mediaFileParser.parse(file.getAbsolutePath());
    }

}
