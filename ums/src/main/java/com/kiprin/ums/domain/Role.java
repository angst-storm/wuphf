package com.kiprin.ums.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "ums", name = "roles")
public class Role {

    @Id
    @Column("id")
    @JsonProperty("roleId")
    private UUID id;

    @Column("name")
    @JsonProperty("role")
    private RoleName name;

    private String description;
}
