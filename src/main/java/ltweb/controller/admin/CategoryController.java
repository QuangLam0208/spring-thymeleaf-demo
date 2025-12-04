package ltweb.controller.admin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import ltweb.entity.Category;
import ltweb.entity.User;
import ltweb.service.CategoryService;
import ltweb.service.UserService;
import ltweb.util.Constant;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @ModelAttribute("users")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("")
    public String list(Model model,
                       @RequestParam(name = "name", required = false) String name,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));

        Page<Category> resultPage;

        if (StringUtils.hasText(name)) {
            resultPage = categoryService.findByNameContaining(name, pageable);
            model.addAttribute("name", name);
        } else {
            resultPage = categoryService.findAll(pageable);
        }

        model.addAttribute("categoryPage", resultPage);

        int totalPages = resultPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/categories/list";
    }

    @GetMapping("add")
    public String add(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        model.addAttribute("isEdit", false);
        return "admin/categories/addOrEdit";
    }

    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        Optional<Category> opt = categoryService.findById(id);
        
        if (opt.isPresent()) {
            Category category = opt.get();
            model.addAttribute("category", category);
            model.addAttribute("isEdit", true);
            return "admin/categories/addOrEdit";
        }
        
        return "redirect:/admin/categories";
    }

    @PostMapping("save")
    public String save(Model model, 
                       @Valid @ModelAttribute("category") Category category,
                       BindingResult result, 
                       @RequestParam("imageFile") MultipartFile file) {
        
        if (result.hasErrors()) {
            // Logic xác định Edit/Add dựa vào ID
            boolean isEdit = category.getId() != null && category.getId() > 0;
            model.addAttribute("isEdit", isEdit);
            return "admin/categories/addOrEdit";
        }

        try {
            if (!file.isEmpty()) {
                File uploadDir = new File(Constant.UPLOAD_DIRECTORY);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String originalFilename = file.getOriginalFilename();
                String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

                Path filePath = Paths.get(Constant.UPLOAD_DIRECTORY, fileName);
                Files.write(filePath, file.getBytes());

                category.setImages(fileName);
            } else {
                if (category.getId() != null) {
                    Category oldCategory = categoryService.findById(category.getId()).orElse(null);
                    if (oldCategory != null) {
                        category.setImages(oldCategory.getImages());
                        
                        // Giữ lại User cũ nếu form không gửi lên
                        if (category.getUser() == null) {
                             category.setUser(oldCategory.getUser());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Upload failed: " + e.getMessage());
            
            // Gửi lại isEdit khi có lỗi Exception
            boolean isEdit = category.getId() != null && category.getId() > 0;
            model.addAttribute("isEdit", isEdit);
            
            return "admin/categories/addOrEdit";
        }
        
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("delete/{id}")
    public String delete(Model model, @PathVariable("id") Integer id) {
        try {
            categoryService.deleteById(id);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/admin/categories";
    }
}