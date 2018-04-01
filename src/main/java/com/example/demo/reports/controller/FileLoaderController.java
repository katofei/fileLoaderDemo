package com.example.demo.reports.controller;

import com.example.demo.reports.QueryExecutor;
import com.example.demo.reports.entity.Content;
import com.example.demo.reports.entity.MediaFile;
import com.example.demo.reports.view.MediaFileExcelView;
import com.example.demo.reports.view.WebContentExcelView;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FileLoaderController {

    private final QueryExecutor queryExecutor;
    private final MediaFileExcelView mediaFileExcelView;
    private final WebContentExcelView webContentExcelView;

    @Autowired
    public FileLoaderController(QueryExecutor queryExecutor, MediaFileExcelView mediaFileExcelView,
                                WebContentExcelView webContentExcelView) {
        this.queryExecutor = queryExecutor;
        this.mediaFileExcelView = mediaFileExcelView;
        this.webContentExcelView = webContentExcelView;
    }


    @PostMapping(value = "/report/loadFile")
    public ModelAndView uploadFile(@RequestParam("name") String[] names,
                                   @RequestParam("file") MultipartFile[] files) throws Exception {
        log.info("uploadFile started");
        ModelAndView modelAndView = new ModelAndView("getStatistic");
        if (files.length != names.length) {
            log.error("Error during files adding");
            modelAndView.addObject("message", "Mandatory information missing");
        }
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String name = names[i];
            try {
                byte[] bytes = file.getBytes();

                // Creating the directory to store file
                String rootPath = System.getProperty("user.home");
                File dir = new File(rootPath + File.separator + "Downloads");
                if (!dir.exists())
                    dir.mkdirs();

                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
                log.info("Server File Location=" + serverFile.getAbsolutePath());

                modelAndView.addObject("message", "You successfully uploaded file=" + name);
            } catch (Exception e) {
                throw new Exception("Files uploading failed" + name);
            }
        }
        return modelAndView;
    }


    @PostMapping(value = "/report/download/{contentId}")
    public String chooseContent(@PathVariable("contentId") String contentId, BindingResult result) {
        log.info("chooseContent started");
        log.info("PathVariable contentId {}", contentId);
        if (result.hasErrors()){
            return "Error";
        }
        if (("webContent").equals(contentId)) {
            return "redirect:/report/download/webContent";
        } else if ("mediaFiles".equals(contentId)) {
            return "redirect:/report/download/mediaFiles";
        }
        return "";
    }


    @GetMapping(value = "/report/download/webContent")
    public String downloadContentInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        log.info("downloadContentInfo started");
        List<Content> allContentList = queryExecutor.getAllWebContent();
        Workbook workbook = new HSSFWorkbook();
        Map<String, Object> map = new HashMap<>();
        map.put("contentList", allContentList);
        webContentExcelView.buildExcelDocument(map, workbook, httpServletRequest, httpServletResponse);
        return "redirect:/report";
    }

    @GetMapping(value = "/report/download/mediaFiles")
    public String downloadMediaInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        log.info("downloadMediaInfo started");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        Map<String, Object> map = new HashMap<>();
        Workbook workbook = new HSSFWorkbook();
        map.put("mediaList", allMedia);
        mediaFileExcelView.buildExcelDocument(map, workbook, httpServletRequest, httpServletResponse);
        return "redirect:/report";
    }

}
