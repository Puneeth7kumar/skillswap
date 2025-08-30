package com.skillswap.skillswaphub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String message;
    private Long skillId;
    private String requesterName;
}

