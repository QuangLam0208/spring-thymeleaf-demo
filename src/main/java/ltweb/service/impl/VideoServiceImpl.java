package ltweb.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ltweb.entity.Video;
import ltweb.repository.VideoRepository;
import ltweb.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Video save(Video entity) {
        // Kiểm tra > 0 nghĩa là đang Cập nhật (Edit)
        if (entity.getId() > 0) {
            // SỬA LỖI Ở ĐÂY: findById trả về Video, không phải Optional<Video>
            Video oldVideo = findById(entity.getId());

            // Kiểm tra khác null thay vì isPresent()
            if (oldVideo != null) { 
                
                // 1. Giữ lại Poster cũ nếu không upload ảnh mới
                if (!StringUtils.hasText(entity.getPoster())) {
                    entity.setPoster(oldVideo.getPoster());
                }

                // 2. Giữ lại Title cũ nếu gửi lên rỗng
                if (!StringUtils.hasText(entity.getTitle())) {
                    entity.setTitle(oldVideo.getTitle());
                }

                // 3. Giữ lại Description cũ nếu gửi lên rỗng
                if (!StringUtils.hasText(entity.getDescription())) {
                    entity.setDescription(oldVideo.getDescription());
                }
                
                // 4. Giữ lại lượt xem cũ nếu giá trị mới là 0
                if (entity.getViews() == 0) {
                     entity.setViews(oldVideo.getViews());
                }
                
                // 5. Giữ lại Category cũ nếu không chọn mới
                if (entity.getCategory() == null) {
                    entity.setCategory(oldVideo.getCategory());
                }
            }
            return videoRepository.save(entity);
        } else {
            // Trường hợp Thêm mới (Add)
            return videoRepository.save(entity);
        }
    }

    @Override
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    @Override
    public List<Video> findByTitleContaining(String title) {
        return videoRepository.findByTitleContaining(title);
    }

    @Override
    public Video findById(Integer id) {
        // Hàm này trả về Video hoặc null
        Optional<Video> video = videoRepository.findById(id);
        return video.orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        videoRepository.deleteById(id);
    }
    
    @Override
    public Page<Video> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    @Override
    public Page<Video> findByTitleContaining(String title, Pageable pageable) {
        return videoRepository.findByTitleContaining(title, pageable);
    }
}