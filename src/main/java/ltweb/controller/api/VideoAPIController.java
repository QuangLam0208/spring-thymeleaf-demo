package ltweb.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ltweb.entity.Category;
import ltweb.entity.Video;
import ltweb.model.Response;
import ltweb.service.CategoryService;
import ltweb.service.VideoService;

@RestController
@RequestMapping(path = "/v1/api/video")
public class VideoAPIController {

	@Autowired
	private VideoService videoService;
	
	@Autowired
	private CategoryService categoryService;
	
	// Lay danh sach co phan trang va tim kiem
	@GetMapping
	public ResponseEntity<Response> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String title) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Video> videoPage;

        if (title != null && !title.isEmpty()) {
            videoPage = videoService.findByTitleContaining(title, pageable);
        } else {
            videoPage = videoService.findAll(pageable);
        }

        return new ResponseEntity<>(
            new Response(true, "Thành công", videoPage), 
            HttpStatus.OK
        );
    }
	
	// API lấy chi tiết Video theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Response> getVideoById(@PathVariable("id") Integer id) {
        Video video = videoService.findById(id);
        if (video != null) {
            return new ResponseEntity<>(new Response(true, "Thành công", video), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
    }
	
	public ResponseEntity<Response> addVideo(@RequestBody Video video) {
		try {
			if (video.getCategory() != null && video.getCategory().getId() != null) {
				Optional<Category> cate = categoryService.findById(video.getCategory().getId());
				cate.ifPresent(video::setCategory);
			}
			if (video.getPoster() == null) video.setPoster("no-image.jpg");
			
			Video savedVideo = videoService.save(video);
			return new ResponseEntity<>(new Response(true, "Thêm thành công", savedVideo), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new Response(false, "Lỗi: " + e.getMessage(), null), HttpStatus.BAD_REQUEST);
		}
	}

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateVideo(@PathVariable("id") Integer id, @RequestBody Video videoData) {
        Video existingVideo = videoService.findById(id);
        if (existingVideo != null) {
            existingVideo.setTitle(videoData.getTitle());
            existingVideo.setDescription(videoData.getDescription());
            existingVideo.setActive(videoData.isActive());
            existingVideo.setViews(videoData.getViews());
            
            if (videoData.getPoster() != null) existingVideo.setPoster(videoData.getPoster());
            
            // Cập nhật Category
            if (videoData.getCategory() != null && videoData.getCategory().getId() != null) {
                 Optional<Category> cat = categoryService.findById(videoData.getCategory().getId());
                 cat.ifPresent(existingVideo::setCategory);
            }

            Video updatedVideo = videoService.save(existingVideo);
            return new ResponseEntity<>(new Response(true, "Cập nhật thành công", updatedVideo), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Không tìm thấy video", null), HttpStatus.NOT_FOUND);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteVideo(@PathVariable("id") Integer id) {
        try {
            videoService.deleteById(id);
            return new ResponseEntity<>(new Response(true, "Xóa thành công", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, "Không thể xóa video này", null), HttpStatus.BAD_REQUEST);
        }
    }
	
}
