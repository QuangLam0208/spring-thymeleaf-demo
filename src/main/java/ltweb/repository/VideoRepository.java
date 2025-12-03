package ltweb.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ltweb.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findByTitleContaining(String title);

    Page<Video> findByTitleContaining(String title, Pageable pageable);

    List<Video> findByActiveTrue();
}