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
        if (entity.getId() == 0) {
            return categoryRepository.save(entity);
        } else {
            // Trường hợp Cập nhật (Edit)
            Optional<Category> opt = findById(entity.getId());
            if (opt.isPresent()) {
                Category oldCategory = opt.get();

                if (!StringUtils.hasText(entity.getName())) {
                    entity.setName(oldCategory.getName());
                }

                if (!StringUtils.hasText(entity.getImages())) {
                    entity.setImages(oldCategory.getImages());
                }
                
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