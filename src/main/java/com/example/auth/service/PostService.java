package com.example.auth.service;

import com.example.auth.model.Post;
import com.example.auth.model.User;
import com.example.auth.model.dto.CreatePostRequest;
import com.example.auth.model.dto.UpdatePostRequest;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章业务逻辑层 —— 实现创建、编辑、删除、列表查询，含作者权限控制
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建文章
     */
    public Post createPost(CreatePostRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
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
     * 编辑文章 —— 仅允许作者本人编辑
     */
    @Transactional
    public Post updatePost(Long postId, UpdatePostRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 权限校验：只有文章作者才能编辑
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("无权编辑他人的文章");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
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