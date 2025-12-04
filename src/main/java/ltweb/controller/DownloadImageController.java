package ltweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import ltweb.util.Constant;

@Controller
public class DownloadImageController {

    @GetMapping("/image")
    public void index(@RequestParam("fname") String fileName, HttpServletResponse response) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        File file = new File(Constant.UPLOAD_DIRECTORY + "/" + fileName);

        if (file.exists()) {
            // Gửi về cho trình duyệt
            FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        }
    }
}
