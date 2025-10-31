package com.mobilewebservices.events.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobilewebservices.util.HtmlCleaningSerializer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDataDto {
    private Integer id;

    @Min(value = 2020, message = "Year must be at least 2020")
    @Max(value = 2030, message = "Year must be at most 2030")
    private Integer year;

    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @Min(value = 1, message = "Date must be between 1 and 31")
    @Max(value = 31, message = "Date must be between 1 and 31")
    private Integer date;

    @Size(max = 100, message = "DateTime string cannot exceed 100 characters")
    private String dateTime;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    @JsonSerialize(using = HtmlCleaningSerializer.class)
    private String title;

    @Size(max = 5000, message = "Event content cannot exceed 5000 characters")
    @JsonSerialize(using = HtmlCleaningSerializer.class)
    private String content;

    @Min(value = 0, message = "Rank must be non-negative")
    @Max(value = 10, message = "Rank must be at most 10")
    private Byte rank;

    @Size(max = 100, message = "Sender name cannot exceed 100 characters")
    private String sender;
}
