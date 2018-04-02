package com.example.demo.reports.file;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FileHelper {
    public static File upload(MultipartFile file, HttpServletRequest request, String uploadFolder) {
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
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));

                stream.write(bytes);
                stream.close();
                return serverFile;
            } else {
                serverFile = null;
            }
        } catch (Exception e) {
            serverFile = null;
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
