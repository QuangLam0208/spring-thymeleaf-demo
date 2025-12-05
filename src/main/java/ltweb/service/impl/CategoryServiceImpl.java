package ltweb.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; 

import ltweb.entity.Category;
import ltweb.repository.CategoryRepository;
import ltweb.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public <S extends Category> S save(S entity) {
        // Kiểm tra null trước tiên vì id là Integer
        if (entity.getId() == null || entity.getId() == 0) {
            return categoryRepository.save(entity);
        } else {
            // Logic cập nhật (Edit)
            Optional<Category> opt = findById(entity.getId());
            if (opt.isPresent()) {
                Category oldCategory = opt.get();
                
                // Giữ lại tên cũ nếu không gửi lên
                if (!StringUtils.hasText(entity.getName())) {
                    entity.setName(oldCategory.getName());
                }
                
                // Giữ lại ảnh cũ nếu không gửi lên
                if (!StringUtils.hasText(entity.getImages())) {
                    entity.setImages(oldCategory.getImages());
                }
                
                // Giữ lại User cũ nếu bị null (tránh mất quan hệ)
                if (entity.getUser() == null) {
                    entity.setUser(oldCategory.getUser());
                }
                
                // Giữ lại Code cũ
                if (!StringUtils.hasText(entity.getCode())) {
                    entity.setCode(oldCategory.getCode());
                }
                
                // Giữ lại trạng thái cũ (tuỳ chọn, vì boolean mặc định là false)
                // entity.setStatus(oldCategory.isStatus());
            }
            return categoryRepository.save(entity);
        }
    }

    @Override
    public List<Category> findByNameContaining(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    @Override
    public Page<Category> findByNameContaining(String name, Pageable pageable) {
        return categoryRepository.findByNameContaining(name, pageable);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }
}