package ltweb.controller.admin;

import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ltweb.entity.Category;
import ltweb.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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

    @GetMapping("/add")
    public String add(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        
        // TRUYỀN BIẾN isEdit = false (Thêm mới)
        model.addAttribute("isEdit", false);
        
        return "admin/categories/addOrEdit";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(Model model, @PathVariable("id") Integer id) {
        Optional<Category> opt = categoryService.findById(id);
        
        if (opt.isPresent()) {
            Category category = opt.get();
            model.addAttribute("category", category);
            
            // TRUYỀN BIẾN isEdit = true (Cập nhật)
            model.addAttribute("isEdit", true);
            
            return new ModelAndView("admin/categories/addOrEdit");
        }
        
        return new ModelAndView("forward:/admin/categories");
    }

    @PostMapping("/save")
    public ModelAndView save(Model model, @ModelAttribute("category") Category category) {
        categoryService.save(category);
        return new ModelAndView("forward:/admin/categories");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(Model model, @PathVariable("id") Integer id) {
        categoryService.deleteById(id);
        return new ModelAndView("forward:/admin/categories");
    }
}