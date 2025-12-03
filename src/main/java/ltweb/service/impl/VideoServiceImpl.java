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
        if (entity.getId() > 0) {
            Video oldVideo = findById(entity.getId());

            if (oldVideo != null) { 
                
                if (!StringUtils.hasText(entity.getPoster())) {
                    entity.setPoster(oldVideo.getPoster());
                }
                
                if (!StringUtils.hasText(entity.getTitle())) {
                    entity.setTitle(oldVideo.getTitle());
                }

                if (!StringUtils.hasText(entity.getDescription())) {
                    entity.setDescription(oldVideo.getDescription());
                }
                
                if (entity.getViews() == 0) {
                     entity.setViews(oldVideo.getViews());
                }
                
                if (entity.getCategory() == null) {
                    entity.setCategory(oldVideo.getCategory());
                }
            }
            return videoRepository.save(entity);
        } else {
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