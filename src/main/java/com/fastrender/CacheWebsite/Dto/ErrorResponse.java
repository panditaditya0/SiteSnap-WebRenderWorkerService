package com.fastrender.CacheWebsite.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponse {
    public LocalDateTime datetime;
    public String message;
    public String details;
}
