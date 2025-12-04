package ltweb.controller.admin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ltweb.entity.Category;
import ltweb.entity.Video;
import ltweb.service.CategoryService;
import ltweb.service.VideoService;
import ltweb.util.Constant;

@Controller
@RequestMapping("/admin/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String list(Model model,
                       @RequestParam(name = "title", required = false) String title,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size) {
        
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        Page<Video> resultPage;
        if (StringUtils.hasText(title)) {
            resultPage = videoService.findByTitleContaining(title, pageable);
            model.addAttribute("keyword", title);
        } else {
            resultPage = videoService.findAll(pageable);
        }

        model.addAttribute("videoPage", resultPage);
        
        int totalPages = resultPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/videos/list";
    }

    @GetMapping("add")
    public String add(Model model, RedirectAttributes redirectAttributes) {
        List<Category> categories = categoryService.findAll();
        
        if (categories == null || categories.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Hệ thống chưa có Danh mục nào. Vui lòng tạo Danh mục trước khi thêm Video!");
            return "redirect:/admin/categories";
        }

        model.addAttribute("video", new Video());
        model.addAttribute("categories", categories);
        model.addAttribute("isEdit", false);
        return "admin/videos/addOrEdit";
    }

    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        Video video = videoService.findById(id);
        if (video != null) {
        	model.addAttribute("video", video);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", true);
            return "admin/videos/addOrEdit";
        }
        return "redirect:/admin/videos";
    }

    @PostMapping("save")
    public String save(Model model, @ModelAttribute("video") Video video,
                       @RequestParam("imageFile") MultipartFile file) {
        
        // Xử lý upload ảnh 
        if (!file.isEmpty()) {
            try {
                File uploadDir = new File(Constant.UPLOAD_DIRECTORY);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                
                Files.write(Paths.get(Constant.UPLOAD_DIRECTORY, fileName), file.getBytes());
                
                video.setPoster(fileName);
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        } else {
            // Giữ ảnh cũ nếu đang edit
            if (video.getId() != null) {
                Video old = videoService.findById(video.getId());
                if (old != null) video.setPoster(old.getPoster());
            }
        }
        
        videoService.save(video);
        return "redirect:/admin/videos";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable("id") Integer id) {
        videoService.deleteById(id);
        return "redirect:/admin/videos";
    }

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.findAll();
    }
}