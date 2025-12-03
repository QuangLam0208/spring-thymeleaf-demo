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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import ltweb.entity.User;
import ltweb.service.UserService;

@Controller
@RequestMapping("admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. Hiển thị danh sách, tìm kiếm, phân trang
    @GetMapping("")
    public String list(Model model,
                       @RequestParam(name = "username", required = false) String username,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size) {
        
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("username"));
        
        Page<User> resultPage;
        
        if (StringUtils.hasText(username)) {
            resultPage = userService.findByUsernameContaining(username, pageable);
            model.addAttribute("username", username);
        } else {
            resultPage = userService.findAll(pageable);
        }

        model.addAttribute("userPage", resultPage);
        
        int totalPages = resultPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        
        return "admin/users/list";
    }

    // 2. Form thêm mới
    @GetMapping("add")
    public String add(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", false);
        return "admin/users/addOrEdit";
    }

    // 3. Form chỉnh sửa
    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer userId) {
        Optional<User> opt = userService.findById(userId);
        if (opt.isPresent()) {
            model.addAttribute("user", opt.get());
            model.addAttribute("isEdit", true);
            return "admin/users/addOrEdit";
        }
        // Dùng RedirectAttributes để báo lỗi nếu không tìm thấy (Tùy chọn)
        return "redirect:/admin/users";
    }

    // 4. Lưu dữ liệu
    @PostMapping("save")
    public String save(Model model, 
                       @Valid @ModelAttribute("user") User user, 
                       BindingResult result, 
                       RedirectAttributes redirectAttributes) { // Thêm RedirectAttributes
        
        // Kiểm tra lỗi Validation
        if (result.hasErrors()) {
            // QUAN TRỌNG: Nếu có lỗi, phải gửi lại isEdit để View hiển thị đúng title
            // Nếu user.getId() > 0 tức là đang Sửa, ngược lại là Thêm
            boolean isEdit = user.getId() != null && user.getId() > 0;
            model.addAttribute("isEdit", isEdit);
            return "admin/users/addOrEdit";
        }
        
        userService.save(user);
        
        // Thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Lưu người dùng thành công!");
        
        return "redirect:/admin/users";
    }

    // 5. Xóa
    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng này (có thể do ràng buộc dữ liệu)!");
            e.printStackTrace();
        }
        return "redirect:/admin/users";
    }
}