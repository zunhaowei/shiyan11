package com.example.auth.controller;
import com.example.auth.model.Post;
import com.example.auth.model.dto.ApiResponse;
import com.example.auth.model.dto.CreatePostRequest;
import com.example.auth.model.dto.UpdatePostRequest;
import com.example.auth.service.PostService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
/**
 * 博客文章控制器 —— 对外暴露文章 CRUD API
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
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
        return ApiResponse.success(postToMap(post));
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
}