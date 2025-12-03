package ltweb.controller;

import java.util.List;
import java.util.Optional;
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
import org.springframework.web.servlet.ModelAndView;

import ltweb.entity.Category;
import ltweb.entity.Video;
import ltweb.service.CategoryService;
import ltweb.service.VideoService;

@Controller
@RequestMapping("/admin/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CategoryService categoryService; // Bổ sung để lấy danh sách Category cho Dropdown

    // --- PHẦN 1: LIST & SEARCH & PAGINATION (Code của bạn) ---
    @GetMapping("")
    public String list(Model model,
                       @RequestParam(name = "title", required = false) String title,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size) {
        
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        Page<Video> resultPage;
        // Lưu ý: Đảm bảo VideoService interface đã có hàm findByTitleContaining(String, Pageable)
        if (StringUtils.hasText(title)) {
            resultPage = videoService.findByTitleContaining(title, pageable);
            model.addAttribute("keyword", title);
        } else {
            // Lưu ý: Đảm bảo VideoService interface đã có hàm findAll(Pageable)
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
    
    // --- PHẦN 2: CÁC CHỨC NĂNG CÒN THIẾU (ADD, EDIT, DELETE) ---

    @GetMapping("/add")
    public String add(Model model) {
        Video video = new Video();
        model.addAttribute("video", video);
        model.addAttribute("isEdit", false);
        return "admin/videos/addOrEdit";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(Model model, @PathVariable("id") Integer id) {
        Video video = videoService.findById(id);
        if (video != null) {
            model.addAttribute("video", video);
            model.addAttribute("isEdit", true);
            return new ModelAndView("admin/videos/addOrEdit");
        }
        return new ModelAndView("forward:/admin/videos");
    }

    @PostMapping("/save")
    public ModelAndView save(Model model, @ModelAttribute("video") Video video) {
        videoService.save(video);
        return new ModelAndView("forward:/admin/videos");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(Model model, @PathVariable("id") Integer id) {
        videoService.deleteById(id);
        return new ModelAndView("forward:/admin/videos");
    }
    
    // --- PHẦN 3: DATA CHO DROPDOWN CATEGORY ---
    // Hàm này sẽ tự động thêm attribute "categories" vào Model cho TẤT CẢ các view trong Controller này
    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.findAll();
    }
}