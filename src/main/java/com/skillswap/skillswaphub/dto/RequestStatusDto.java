package com.skillswap.skillswaphub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Request Status Update DTO
public class RequestStatusDto {
    @NotBlank(message = "Status is required")
    private String status; // "accepted" or "rejected"

}
