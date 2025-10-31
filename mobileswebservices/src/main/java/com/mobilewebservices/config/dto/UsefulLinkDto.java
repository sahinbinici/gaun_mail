package com.mobilewebservices.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsefulLinkDto {

    @NotBlank(message = "ID is required")
    @Size(max = 50, message = "ID must not exceed 50 characters")
    private String id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Icon is required")
    @Size(max = 100, message = "Icon name must not exceed 100 characters")
    private String icon;

    @NotBlank(message = "URL is required")
    private String url;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Color is required")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code (e.g., #CC0000)")
    private String color;

    private Integer order;

    private Boolean isActive;
}
