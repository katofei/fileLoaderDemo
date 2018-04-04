package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.entity.WebContent;
import com.example.demo.reports.file.FileHelper;
import com.example.demo.reports.file.MediaFileParser;
import com.example.demo.reports.file.WebContentFileParser;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class FileLoaderController {

    private final QueryExecutor queryExecutor;
    private final WebContentFileParser contentFileParser;
    private final MediaFileParser mediaFileParser;
    private final FileHelper fileHelper;
    private static final String mediaFilePrefix = "media-file-details";
    private static final String webFilePrefix = "web-content-details";

    @Autowired
    public FileLoaderController(QueryExecutor queryExecutor, WebContentFileParser contentFileParser,
                                MediaFileParser mediaFileParser, FileHelper fileHelper) {
        this.queryExecutor = queryExecutor;
        this.contentFileParser = contentFileParser;
        this.mediaFileParser = mediaFileParser;
        this.fileHelper = fileHelper;
    }

    @GetMapping(value = "/report/download/webContent")
    public String downloadContentInfo(Model model) throws Exception {
        log.info("downloadContentInfo started");
        model.addAttribute("contentList", queryExecutor.getAllWebContent());
        model.addAttribute("xlsType", "web content");
        return "";
    }

    @GetMapping(value = "/report/download/mediaFiles")
    public String downloadMediaInfo(Model model) throws Exception {
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
        log.info("Original file name {}", originalFilename);
        if (originalFilename.contains(webFilePrefix)) {
            List<WebContent> webContentList = instantiateAndReadWebFile(file, request);
            request.getSession().setAttribute("compareWhat", webContentList);
            redirectAttributes.addFlashAttribute("successMessage", "File " + originalFilename
                    + " successfully uploaded");
        } else if (originalFilename.contains(mediaFilePrefix)) {
            List<MediaFile> mediaFileList = instantiateAndReaMediaFile(file, request);
            request.getSession().setAttribute("compareWhat", mediaFileList);
            redirectAttributes.addFlashAttribute("successMessage", "File " + originalFilename
                    + " successfully uploaded");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Can not parse  " +
                    file.getOriginalFilename() + " .Please, provide correct name");
        }
        return ("redirect:/report");
    }

    @PostMapping(value = "/report/loadMultiFiles")
    public String multiFileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        log.info("multiFileUpload started");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles("multipartFiles");
        List<String> originalFilenames = new ArrayList<>();
        for (MultipartFile file : files) {
            originalFilenames.add(file.getOriginalFilename());
        }
        if (ifFileNamesEquals(originalFilenames)) {
            if (originalFilenames.get(0).contains(webFilePrefix)) {
                List<WebContent> compareWhat = instantiateAndReadWebFile(files.get(0), request);
                request.getSession().setAttribute("compareWhat", compareWhat);
                List<WebContent> compareWith = instantiateAndReadWebFile(files.get(1), request);
                request.getSession().setAttribute("compareWith", compareWith);
                redirectAttributes.addFlashAttribute("successMessage", "Files successfully added");
            } else if (originalFilenames.get(0).contains(mediaFilePrefix)) {
                List<MediaFile> compareWhat = instantiateAndReaMediaFile(files.get(0), request);
                request.getSession().setAttribute("compareWhat", compareWhat);
                List<MediaFile> compareWith = instantiateAndReaMediaFile(files.get(1), request);
                request.getSession().setAttribute("compareWith", compareWith);
                redirectAttributes.addFlashAttribute("successMessage", "Files successfully added");
            }
        }
        else {
            redirectAttributes.addFlashAttribute("errorMessage", "Files have incompatible types. " +
                    "Please, provide files of tha same format");
        }
        return ("redirect:/report");
    }

    private List<WebContent> initializeContentSheet(File file) throws IOException {
        return contentFileParser.parse(file.getAbsolutePath());
    }

    private List<MediaFile> initializeMediaSheet(File file) throws IOException {
        return mediaFileParser.parse(file.getAbsolutePath());
    }

    private List<WebContent> instantiateAndReadWebFile(MultipartFile file, HttpServletRequest request) throws Exception {
        File report = fileHelper.upload(file, request, "xlsReports");
        List<WebContent> contentFromFileList = initializeContentSheet(report);
        fileHelper.delete(report);
        return contentFromFileList;
    }

    private List<MediaFile> instantiateAndReaMediaFile(MultipartFile file, HttpServletRequest request) throws Exception {
        File report = fileHelper.upload(file, request, "xlsReports");
        List<MediaFile> mediaFileList = initializeMediaSheet(report);
        fileHelper.delete(report);
        return mediaFileList;
    }

    private boolean ifFileNamesEquals(List<String> names) {
        return (names.get(0).contains(webFilePrefix) && names.get(1).contains(webFilePrefix))
                || (names.get(0).contains(mediaFilePrefix) && names.get(1).contains(mediaFilePrefix));
    }
}

