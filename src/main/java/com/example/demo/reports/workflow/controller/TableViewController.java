package com.example.demo.reports.workflow.controller;

import com.example.demo.reports.workflow.model.Content;
import com.example.demo.reports.workflow.model.MediaFile;
import com.example.demo.reports.workflow.model.WebContent;
import com.example.demo.reports.workflow.service.file.FileHelper;
import com.example.demo.reports.workflow.service.file.parser.MediaFileParser;
import com.example.demo.reports.workflow.service.file.parser.WebContentFileParser;
import com.example.demo.reports.workflow.service.query.QueryExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@Slf4j
public class TableViewController {

    private final QueryExecutor queryExecutor;
    private final WebContentFileParser contentFileParser;
    private final MediaFileParser mediaFileParser;
    private final FileHelper fileHelper;
    private static final String mediaFilePrefix = "media-file-details";
    private static final String webFilePrefix = "web-content-details";

    @Autowired
    public TableViewController(QueryExecutor queryExecutor, WebContentFileParser contentFileParser,
                               MediaFileParser mediaFileParser, FileHelper fileHelper) {
        this.queryExecutor = queryExecutor;
        this.contentFileParser = contentFileParser;
        this.mediaFileParser = mediaFileParser;
        this.fileHelper = fileHelper;
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
    public ModelAndView viewCurrentWebContent() throws Exception {
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
    public ModelAndView viewCurrentMediaFiles() throws Exception {
        log.info("viewCurrentMediaFiles started");
        ModelAndView modelAndView = new ModelAndView("report");
        List<MediaFile> allMedia = queryExecutor.getAllMediaFiles();
        if (allMedia.isEmpty()) {
            modelAndView.addObject("errorMessage", "Error!!\n Media files list returned empty");
        }
        modelAndView.addObject("mediaList", allMedia);
        return modelAndView;

    }

    @PostMapping(value = "/report/compareSingleFile")
    public String singleFileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        log.info("singleFileUpload started");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("multipartFile");
        String originalFilename = file.getOriginalFilename();
        log.info("Original file name {}", originalFilename);
        if (originalFilename.contains(webFilePrefix)) {
            List<? extends Content> webContentList = instantiateAndReadWebFile(file, request);
            redirectAttributes.addFlashAttribute("compareWhat", webContentList);
            redirectAttributes.addFlashAttribute("successMessage", "File " + originalFilename
                    + " successfully uploaded");
        } else if (originalFilename.contains(mediaFilePrefix)) {
            List<? extends Content> mediaFileList = instantiateAndReaMediaFile(file, request);
            redirectAttributes.addFlashAttribute("compareWhat", mediaFileList);
            redirectAttributes.addFlashAttribute("successMessage", "File " + originalFilename
                    + " successfully uploaded");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Can not parse  " +
                    file.getOriginalFilename() + " .Please, provide file with correct name");
        }
        return ("redirect:/report/compareSingleFile");
    }


    @GetMapping(value = "/report/compareSingleFile")
    public ModelAndView compareFileWithServer(ModelMap model) throws Exception {
        log.info("compareFileWithServer started");
        ModelAndView modelAndView = new ModelAndView("report");
        String success = (String) model.get("successMessage");
        if (success != null) {
            modelAndView.addObject("successMessage", success);
            @SuppressWarnings("unchecked")
            List<Content> listFromFile = (List<Content>) model.get("compareWhat");
            if (!listFromFile.isEmpty())
                if (listFromFile.get(0) instanceof MediaFile) {
                    List<MediaFile> mediaFileListFromServer = queryExecutor.getAllMediaFiles();
                    if (mediaFileListFromServer.isEmpty()) {
                        modelAndView.addObject("errorMessage", "Error!!\n Media files form server returned empty");
                    } else {
                        compareMediaFiles(mediaFileListFromServer, listFromFile, modelAndView);
                    }
                } else if (listFromFile.get(0) instanceof WebContent) {
                    List<WebContent> contentListFromServer = queryExecutor.getAllWebContent();
                    if (contentListFromServer.isEmpty()) {
                        modelAndView.addObject("errorMessage", "Error!!\n Web content  form server returned empty");
                    } else {
                        compareWebContent(contentListFromServer, listFromFile, modelAndView);
                    }
                }
        } else {
            String errorMessage = (String) model.get("errorMessage");
            modelAndView.addObject("errorMessage", errorMessage);
        }

        return modelAndView;
    }


    @PostMapping(value = "/report/compareMultiFile")
    public String multiFileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        log.info("multiFileUpload started");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles("multipartFiles");
        List<String> originalFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            originalFileNames.add(file.getOriginalFilename());
        }
        if (isFilesCompatible(originalFileNames, redirectAttributes)) {
            if (originalFileNames.get(0).contains(webFilePrefix)) {
                List<? extends Content> compareWhat = instantiateAndReadWebFile(files.get(0), request);
                redirectAttributes.addFlashAttribute("compareWhat", compareWhat);

                List<? extends Content> compareWith = instantiateAndReadWebFile(files.get(1), request);
                redirectAttributes.addFlashAttribute("compareWith", compareWith);
            } else if (originalFileNames.get(0).contains(mediaFilePrefix)) {
                List<? extends Content> compareWhat = instantiateAndReaMediaFile(files.get(0), request);
                redirectAttributes.addFlashAttribute("compareWhat", compareWhat);

                List<? extends Content> compareWith = instantiateAndReaMediaFile(files.get(1), request);
                redirectAttributes.addFlashAttribute("compareWith", compareWith);
            }
        }
        return ("redirect:/report/compareMultiFile");
    }

    @GetMapping(value = "/report/compareMultiFile")
    public ModelAndView compareFiles(ModelMap model) throws Exception {
        log.info("compareFiles started");
        ModelAndView modelAndView = new ModelAndView("report");
        String success = (String) model.get("successMessage");
        if (success != null) {
            modelAndView.addObject("successMessage", success);
            @SuppressWarnings("unchecked")
            List<Content> listFromFirstFile = (List<Content>) model.get("compareWhat");
            @SuppressWarnings("unchecked")
            List<Content> listFromSecondFile = (List<Content>) model.get("compareWith");
            if (!(listFromFirstFile.isEmpty()) && !(listFromSecondFile.isEmpty())) {
                if (listFromFirstFile.get(0) instanceof MediaFile) {
                    compareMediaFiles(listFromFirstFile, listFromSecondFile, modelAndView);
                } else if (listFromFirstFile.get(0) instanceof WebContent) {
                    compareWebContent(listFromFirstFile, listFromSecondFile, modelAndView);
                }
            }
        } else {
            String errorMessage = (String) model.get("errorMessage");
            modelAndView.addObject("errorMessage", errorMessage);
        }
        return modelAndView;
    }

    private static boolean compareLists(List<? extends Content> firstList, List<? extends Content> secondList) {
        return firstList.stream().anyMatch(elem -> secondList.containsAll(firstList));
    }


    private void compareWebContent(List<? extends Content> serverList, List<? extends Content> fileList, ModelAndView modelAndView) {
        if (compareLists(serverList, fileList)) {
            modelAndView.addObject("allContentList", serverList);
            modelAndView.addObject("successMessage", "No differences found");
        } else {
            ModelMap modelMap = new ModelMap();
            ModelMap map = joinLists(serverList, fileList, modelMap);
            modelAndView.addAllObjects(map);
            modelAndView.addObject("errorMessage", "Your resources are not equal");
        }
    }

    private void compareMediaFiles(List<? extends Content> serverList, List<? extends Content> fileList, ModelAndView modelAndView) {
        if (compareLists(serverList, fileList)) {
            modelAndView.addObject("mediaList", serverList);
            modelAndView.addObject("successMessage", "No differences found");
        } else {
            ModelMap modelMap = new ModelMap();
            ModelMap map = joinLists(serverList, fileList, modelMap);
            modelAndView.addAllObjects(map);
            modelAndView.addObject("errorMessage", "Your resources are not equal");
        }
    }

    private ModelMap joinLists(List<? extends Content> firstList, List<? extends Content> secondList, ModelMap modelMap) {
        ArrayList<Content> elementsFromFirst = new ArrayList<>(CollectionUtils.subtract(firstList, secondList));
        ArrayList<Content> elementsFromSecond = new ArrayList<>(CollectionUtils.subtract(secondList, firstList));
        List<Content> mergedElements = new ArrayList<>();
        for (Content fromFirstList : elementsFromFirst) {
            for (Content fromSecondList : elementsFromSecond) {
                if (fromFirstList.getId().equals(fromSecondList.getId()) && !(fromFirstList.equals(secondList))) {
                    mergedElements.add(fromFirstList);
                    mergedElements.add(fromSecondList);
                }
            }
        }
        List<Content> joinedList = Stream.concat(elementsFromFirst.stream(), elementsFromSecond.stream()).sorted().collect(Collectors.toList());
        joinedList.removeAll(mergedElements);
        modelMap.addAttribute("mergedElements", mergedElements);
        modelMap.addAttribute("uniqueElements", joinedList);
        return modelMap;
    }

    private List<? extends Content> instantiateAndReadWebFile(MultipartFile file, HttpServletRequest request) throws Exception {
        log.info("instantiateAndReadWebFile started");
        File report = fileHelper.upload(file, request, "xlsReports");
        List<? extends Content> contentFromFileList = initializeContentSheet(report);
        contentFromFileList.forEach(content -> content.setResource(file.getOriginalFilename()));
        return contentFromFileList;
    }

    private List<? extends Content> initializeContentSheet(File file) throws IOException {
        log.info("initializeContentSheet started");
        return contentFileParser.parse(file.getAbsolutePath());
    }

    private List<? extends Content> instantiateAndReaMediaFile(MultipartFile file, HttpServletRequest request) throws Exception {
        log.info("instantiateAndReaMediaFile started");
        File report = fileHelper.upload(file, request, "xlsReports");
        List<? extends Content> mediaFileList = initializeMediaSheet(report);
        mediaFileList.forEach(media -> media.setResource(file.getOriginalFilename()));
        return mediaFileList;
    }

    private List<? extends Content> initializeMediaSheet(File file) throws IOException {
        log.info("initializeMediaSheet started");
        return mediaFileParser.parse(file.getAbsolutePath());
    }

    private boolean isFilesCompatible(List<String> names, RedirectAttributes redirectAttributes) {
        log.info("isFilesCompatible started");
        boolean result = false;
        if (!names.get(0).equals(names.get(1))) {
            if (names.stream().allMatch(name -> name.contains(webFilePrefix))) {
                result = true;
                redirectAttributes.addFlashAttribute("successMessage", "Files successfully added");
            } else if (names.stream().allMatch(name -> name.contains(mediaFilePrefix))) {
                result = true;
                redirectAttributes.addFlashAttribute("successMessage", "Files successfully added");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Files have incompatible types.\n Please, provide files of tha same format");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error !!! You have uploaded the same file twice.\n Please, select different files");
        }
        return result;
    }
 }
