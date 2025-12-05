package ltweb.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ltweb.entity.Category;
import ltweb.model.Response;
import ltweb.service.CategoryService;

@RestController
@RequestMapping(path = "/v1/api/category")
public class CategoryAPIController {
	@Autowired
	private CategoryService categoryService;
	
	// Lấy tất cả - GET
	@GetMapping
	public ResponseEntity<Response> getAllCategory() {
		return new ResponseEntity<>(
			new Response(true, "Thành công", categoryService.findAll()),
			HttpStatus.OK
		);
	}
	
	// Thêm mới - POST
	// Dữ liệu nhận vào là json thông qua @RequestBody
	@PostMapping("/save")
	public ResponseEntity<Response> addCategory(@RequestBody Category category) {
		try {
			if (category.getImages() == null) {
				category.setImages("default.jpg");
			}
			Category savedCategory = categoryService.save(category);
			return new ResponseEntity<> (
					new Response(true, "Thêm thành công", savedCategory), 
	                HttpStatus.OK
			);
		} catch (Exception e) {
			return new ResponseEntity<> (
					new Response(false, "Thêm thất bại: " + e.getMessage(), null),
					HttpStatus.BAD_REQUEST
			);
		}
	}
	
	// Cap nhat - PUT
	@PutMapping("/update/{id}")
	public ResponseEntity<Response> updateCategory(@PathVariable("id") Integer id, 
			@RequestBody Category categoryData) {
		Optional<Category> optCategory = categoryService.findById(id);
		
		if (optCategory.isPresent()) {
			Category existingCategory = optCategory.get();
            // Cập nhật thông tin từ JSON gửi lên
            existingCategory.setName(categoryData.getName());
            existingCategory.setCode(categoryData.getCode());
            existingCategory.setStatus(categoryData.isStatus());
            if (categoryData.getImages() != null) {
                existingCategory.setImages(categoryData.getImages());
            }
            
            Category updatedCategory = categoryService.save(existingCategory);
            return new ResponseEntity<>(
	                new Response(true, "Cập nhật thành công", updatedCategory), 
	                HttpStatus.OK
            );
		} else {
			return new ResponseEntity<>(
	                new Response(false, "Không tìm thấy danh mục", null), 
	                HttpStatus.NOT_FOUND
	        );
		}
	}
	
	// DELETE
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> deleteCategory(@PathVariable("id") Integer id) {
		Optional<Category> optCategory = categoryService.findById(id);
		
		if (optCategory.isPresent()) {
            categoryService.deleteById(id);
            return new ResponseEntity<>(
                new Response(true, "Xóa thành công", null), 
                HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                new Response(false, "Không tìm thấy danh mục để xóa", null), 
                HttpStatus.NOT_FOUND
            );
        }
	}
}
