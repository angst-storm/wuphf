package com.kiprin.ums.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(schema = "ums", name = "sessions")
public class Session implements Persistable<UUID> {

    @Id
    @JsonProperty("sessionId")
    private UUID id;

    @Column("user_id")
    @JsonIgnore
    private UUID userId;

    @Column("logged_in_at")
    private LocalDateTime loggedInAt;

    @Column("logged_out_at")
    private LocalDateTime loggedOutAt;

    @Transient
    @JsonIgnore
    private boolean newEntity = true;

    @Override
    @JsonIgnore
    public boolean isNew() {
        return newEntity;
    }

    public void markNotNew() {
        this.newEntity = false;
    }
}
