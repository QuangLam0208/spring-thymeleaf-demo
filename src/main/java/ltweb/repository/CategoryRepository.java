package ltweb.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ltweb.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	//Tìm Kiếm theo nội dung tên
	List<Category> findByNameContaining(String name);


	//Tìm kiếm và Phân trang
	Page<Category> findByNameContaining(String name,Pageable pageable);
}
