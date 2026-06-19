package com.example.auth.repository;

import com.example.auth.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问层
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 按文章 ID 查询所有评论（按时间正序）
     */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    /**
     * 统计文章评论数
     */
    long countByPostId(Long postId);
}