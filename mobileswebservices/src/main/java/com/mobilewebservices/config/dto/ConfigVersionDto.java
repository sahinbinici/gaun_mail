package com.mobilewebservices.config.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigVersionDto {

    private String version;

    private String lastUpdated;

    private String minAppVersion;
}
