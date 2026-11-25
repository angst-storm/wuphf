package com.kiprin.messages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMessageRequest {

    @NotBlank
    @Size(max = 280)
    private String text;
}
