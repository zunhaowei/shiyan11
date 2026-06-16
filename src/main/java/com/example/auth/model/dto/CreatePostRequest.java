package com.example.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建文章请求 DTO
 */
@Data
public class CreatePostRequest {

    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度需在 1-200 之间")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}