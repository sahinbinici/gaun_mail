package com.mobilewebservices.news.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobilewebservices.util.HtmlCleaningSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private LocalDateTime postDate;

    @NotBlank(message = "Post title is required")
    @Size(min = 3, max = 255, message = "Post title must be between 3 and 255 characters")
    @JsonSerialize(using = HtmlCleaningSerializer.class)
    private String postTitle;

    @Size(max = 50000, message = "Post content cannot exceed 50000 characters")
    @JsonSerialize(using = HtmlCleaningSerializer.class)
    private String postContent;

    @Size(max = 255, message = "Post name cannot exceed 255 characters")
    private String postName;

    @Size(max = 50, message = "Post type cannot exceed 50 characters")
    private String postType;

    private String haberLinki;
}
