package com.example.auth.controller;

import com.example.auth.model.Comment;
import com.example.auth.model.Post;
import com.example.auth.model.dto.ApiResponse;
import com.example.auth.model.dto.CommentRequest;
import com.example.auth.model.dto.CreatePostRequest;
import com.example.auth.model.dto.UpdatePostRequest;
import com.example.auth.service.CommentService;
import com.example.auth.service.PostService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 博客文章控制器 —— 对外暴露文章 CRUD API，包含评论功能
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * 创建文章  POST /api/posts
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> createPost(@Valid @RequestBody CreatePostRequest request,
                                                        Principal principal) {
        Post post = postService.createPost(request, principal.getName());
        Map<String, Object> data = postToMap(post);
        return ApiResponse.success("文章创建成功", data);
    }

    /**
     * 获取所有文章  GET /api/posts
     */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        List<Map<String, Object>> list = posts.stream()
                .map(this::postToMap)
                .toList();
        return ApiResponse.success(list);
    }

    /**
     * 获取我的文章  GET /api/posts/mine
     */
    @GetMapping("/mine")
    public ApiResponse<List<Map<String, Object>>> getMyPosts(Principal principal) {
        List<Post> posts = postService.getMyPosts(principal.getName());
        List<Map<String, Object>> list = posts.stream()
                .map(this::postToMap)
                .toList();
        return ApiResponse.success(list);
    }

    /**
     * 获取单篇文章  GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        Map<String, Object> result = postToMap(post);
        // 添加评论数
        result.put("commentCount", commentService.countCommentsByPostId(id));
        return ApiResponse.success(result);
    }

    /**
     * 编辑文章  PUT /api/posts/{id}
     * （仅作者本人可操作）
     */
    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> updatePost(@PathVariable Long id,
                                                        @Valid @RequestBody UpdatePostRequest request,
                                                        Principal principal) {
        Post post = postService.updatePost(id, request, principal.getName());
        return ApiResponse.success("文章更新成功", postToMap(post));
    }

    /**
     * 删除文章  DELETE /api/posts/{id}
     * （仅作者本人可操作）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePost(@PathVariable Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
        return ApiResponse.success("文章删除成功");
    }

    // ==================== 评论相关 API ====================

    /**
     * 发表评论  POST /api/posts/{id}/comments
     */
    @PostMapping("/{id}/comments")
    public ApiResponse<Map<String, Object>> createComment(@PathVariable Long id,
                                                           @Valid @RequestBody CommentRequest request,
                                                           Principal principal) {
        Comment comment = commentService.createComment(id, request.getContent(), principal.getName());
        return ApiResponse.success("评论发表成功", commentToMap(comment));
    }

    /**
     * 获取文章评论列表  GET /api/posts/{id}/comments
     */
    @GetMapping("/{id}/comments")
    public ApiResponse<List<Map<String, Object>>> getComments(@PathVariable Long id) {
        List<Comment> comments = commentService.getCommentsByPostId(id);
        List<Map<String, Object>> list = comments.stream()
                .map(this::commentToMap)
                .toList();
        return ApiResponse.success(list);
    }

    /**
     * 将 Post 实体转为 Map（避免循环引用，同时返回 author 信息）
     */
    private Map<String, Object> postToMap(Post post) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", post.getId());
        map.put("title", post.getTitle());
        map.put("content", post.getContent());
        map.put("author", post.getAuthor().getUsername());
        map.put("createdAt", post.getCreatedAt());
        map.put("updatedAt", post.getUpdatedAt());
        return map;
    }

    /**
     * 将 Comment 实体转为 Map（避免循环引用）
     */
    private Map<String, Object> commentToMap(Comment comment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", comment.getId());
        map.put("content", comment.getContent());
        map.put("username", comment.getUser().getUsername());
        map.put("postId", comment.getPost().getId());
        map.put("createdAt", comment.getCreatedAt());
        return map;
    }
}