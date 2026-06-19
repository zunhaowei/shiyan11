package com.example.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发表评论请求 DTO
 */
@Data
public class CommentRequest {

    @NotBlank(message = "评论内容不能为空")
    private String content;
}