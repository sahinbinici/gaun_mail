package com.mobilewebservices.announcements.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobilewebservices.util.HtmlCleaningSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;

    @NotBlank(message = "Article title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    @JsonSerialize(using = HtmlCleaningSerializer.class)
    private String title;

    @NotBlank(message = "Article content is required")
    @Size(min = 10, max = 10000, message = "Content must be between 10 and 10000 characters")
    @JsonSerialize(using = HtmlCleaningSerializer.class)
    private String text;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;
}
