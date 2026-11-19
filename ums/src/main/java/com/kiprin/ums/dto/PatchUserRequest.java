package com.kiprin.ums.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class PatchUserRequest {

    private String name;

    @Email
    private String email;

    @JsonAlias("password")
    private String passwordHash;
}
