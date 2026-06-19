package com.example.auth.service;

import com.example.auth.model.Comment;
import com.example.auth.model.Post;
import com.example.auth.model.User;
import com.example.auth.repository.CommentRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.SensitiveWordFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论业务逻辑层 —— 实现评论的发表、查询功能，包含敏感词过滤
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SensitiveWordFilter sensitiveWordFilter;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          SensitiveWordFilter sensitiveWordFilter) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.sensitiveWordFilter = sensitiveWordFilter;
    }

    /**
     * 发表评论
     * @param postId 文章ID
     * @param content 评论内容
     * @param username 评论者用户名
     * @return 评论实体
     */
    @Transactional
    public Comment createComment(Long postId, String content, String username) {
        // 获取文章
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 获取评论者
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 敏感词过滤
        String filteredContent = sensitiveWordFilter.filter(content);

        // 创建评论
        Comment comment = Comment.builder()
                .content(filteredContent)
                .post(post)
                .user(user)
                .build();

        return commentRepository.save(comment);
    }

    /**
     * 获取文章的所有评论
     * @param postId 文章ID
     * @return 评论列表（按时间正序）
     */
    public List<Comment> getCommentsByPostId(Long postId) {
        // 验证文章存在
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("文章不存在");
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    /**
     * 获取文章评论数
     * @param postId 文章ID
     * @return 评论数量
     */
    public long countCommentsByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}