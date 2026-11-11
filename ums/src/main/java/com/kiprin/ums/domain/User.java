package com.kiprin.ums.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(schema = "ums", name = "users")
public class User implements Persistable<UUID> {

    @Id
    private UUID id;

    private String name;

    private String email;

    @Column("password_hash")
    @JsonAlias("password")
    @JsonProperty(value = "passwordHash", access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

    @MappedCollection(idColumn = "user_id")
    @JsonIgnore
    private Set<UserRole> roleRefs = new HashSet<>();

    @Transient
    private List<Role> roles = new ArrayList<>();

    @Transient
    private Session lastSession;

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

    public void assignRoleId(UUID roleId) {
        if (roleId == null) {
            return;
        }
        if (roleRefs == null) {
            roleRefs = new HashSet<>();
        }
        roleRefs.add(new UserRole(roleId));
    }

    public void replaceRoleIds(Set<UUID> roleIds) {
        if (roleRefs == null) {
            roleRefs = new HashSet<>();
        } else {
            roleRefs.clear();
        }
        if (roleIds != null) {
            roleIds.forEach(this::assignRoleId);
        }
    }

    public boolean hasRole(RoleName roleName) {
        return roles != null && roles.stream().anyMatch(r -> roleName.equals(r.getName()));
    }
}
