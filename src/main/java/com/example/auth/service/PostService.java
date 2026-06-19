package com.example.auth.service;

import com.example.auth.model.Post;
import com.example.auth.model.User;
import com.example.auth.model.dto.CreatePostRequest;
import com.example.auth.model.dto.UpdatePostRequest;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.SensitiveWordFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章业务逻辑层 —— 实现创建、编辑、删除、列表查询，含作者权限控制和敏感词过滤
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SensitiveWordFilter sensitiveWordFilter;

    public PostService(PostRepository postRepository, UserRepository userRepository,
                       SensitiveWordFilter sensitiveWordFilter) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.sensitiveWordFilter = sensitiveWordFilter;
    }

    /**
     * 创建文章（包含敏感词过滤）
     */
    public Post createPost(CreatePostRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 敏感词过滤
        String filteredTitle = sensitiveWordFilter.filter(request.getTitle());
        String filteredContent = sensitiveWordFilter.filter(request.getContent());

        Post post = Post.builder()
                .title(filteredTitle)
                .content(filteredContent)
                .author(author)
                .build();

        return postRepository.save(post);
    }

    /**
     * 获取所有文章列表
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 获取当前用户的文章列表
     */
    public List<Post> getMyPosts(String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(author.getId());
    }

    /**
     * 根据 ID 获取单篇文章
     */
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
    }

    /**
     * 编辑文章 —— 仅允许作者本人编辑（包含敏感词过滤）
     */
    @Transactional
    public Post updatePost(Long postId, UpdatePostRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 权限校验：只有文章作者才能编辑
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("无权编辑他人的文章");
        }

        // 敏感词过滤
        String filteredTitle = sensitiveWordFilter.filter(request.getTitle());
        String filteredContent = sensitiveWordFilter.filter(request.getContent());

        post.setTitle(filteredTitle);
        post.setContent(filteredContent);
        return postRepository.save(post);
    }

    /**
     * 删除文章 —— 仅允许作者本人删除
     */
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 权限校验：只有文章作者才能删除
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("无权删除他人的文章");
        }

        postRepository.delete(post);
    }
}