package com.kiprin.messages.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(schema = "messages", name = "messages")
public class Message implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("author_id")
    private UUID authorId;

    private String text;

    @Column("created_at")
    private LocalDateTime createdAt;

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
