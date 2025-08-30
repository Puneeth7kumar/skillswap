package com.skillswap.skillswaphub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Request Creation DTO
public class RequestCreateDto {
    @NotNull(message = "Skill ID is required")
    private Long skillId;

    private String message;

}
