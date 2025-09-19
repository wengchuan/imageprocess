package com.imageprocess.repository;

import com.imageprocess.model.Images;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images,Long> {

    public List<Images> findAllByUserId(Long userId, Pageable pageable);
    public List<Images> findByUserId(Long userId);
    public Images findByUserIdAndId(long userId, long id);

}
