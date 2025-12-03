package ltweb.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ltweb.entity.Video;

public interface VideoService {

	void deleteById(Integer id);

	Video findById(Integer id);

	List<Video> findByTitleContaining(String title);

	List<Video> findAll();

	Video save(Video entity);
	
	Page<Video> findAll(Pageable pageable);
    Page<Video> findByTitleContaining(String title, Pageable pageable);

}
