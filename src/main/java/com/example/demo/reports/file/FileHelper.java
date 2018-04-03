package com.example.demo.reports.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Slf4j
public class FileHelper {
    public static File upload(MultipartFile file, HttpServletRequest request, String uploadFolder) throws Exception {
       log.info(" upload () in FileHelper.class started");
        String fileName;
        File serverFile;
        try {
            if (!file.isEmpty()) {
                String applicationPath = request.getServletContext().getRealPath("");
                fileName = file.getOriginalFilename();
                byte[] bytes = file.getBytes();

                String rootPath = applicationPath;
                File dir = new File(rootPath + File.separator + uploadFolder);

                if (!dir.exists())
                    dir.mkdirs();
                serverFile = new File(dir.getAbsolutePath()
                        + File.separator + fileName);
                log.info("Server file is {}", serverFile.getAbsolutePath());
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));

                stream.write(bytes);
                stream.close();
                return serverFile;
            } else {
                log.error("File is empty");
                serverFile = null;
            }
        } catch (Exception e) {
            throw new Exception("Error during adding file to server");
        }
        return serverFile;
    }

    public static void delete(File file) {
        try {
            FileUtils.cleanDirectory(file.getParentFile().getParentFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
