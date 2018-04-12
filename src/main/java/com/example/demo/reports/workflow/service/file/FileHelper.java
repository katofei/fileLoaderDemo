package com.example.demo.reports.workflow.service.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Service
@Slf4j
public class FileHelper {

    public final File upload(MultipartFile file, HttpServletRequest request, String uploadFolder) throws Exception {
        log.info(" upload () in FileHelper.class started");
        String fileName;
        File serverFile;
        try {
            if (!file.isEmpty()) {
                String applicationPath = request.getSession().getServletContext().getRealPath("");
                fileName = file.getOriginalFilename();
                byte[] bytes = file.getBytes();

                File dir = new File(applicationPath + File.separator + uploadFolder);

                if (!dir.exists())
                    dir.mkdirs();
                serverFile = new File(dir.getAbsolutePath()
                        + File.separator + fileName);
                log.info("Server file is {}", serverFile.getAbsolutePath());

                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                    stream.write(bytes);
                }

            } else {
                log.error("File is empty");
                serverFile = null;
            }
        } catch (Exception e) {
            throw new Exception("Error during adding file to server");
        }
        return serverFile;
    }

    public final void delete(File file) {
        try {
            FileUtils.cleanDirectory(file.getParentFile().getParentFile());
            log.info(file.getAbsolutePath(), " {} successfully deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
