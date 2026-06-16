package com.example.auth.repository;

import com.example.auth.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章数据访问层
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 按作者 ID 查询所有文章（按创建时间倒序）
     */
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    /**
     * 查询所有文章（按创建时间倒序）
     */
    List<Post> findAllByOrderByCreatedAtDesc();
}