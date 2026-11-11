package com.kiprin.ums.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class PatchUserRequest {
    private String name;
    private String email;

    @JsonAlias("password")
    private String passwordHash;
}
