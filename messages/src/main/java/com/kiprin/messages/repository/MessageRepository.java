package com.kiprin.messages.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.kiprin.messages.domain.Message;

public interface MessageRepository extends CrudRepository<Message, UUID> {

    List<Message> findByAuthorIdOrderByCreatedAtDescIdDesc(UUID authorId);

    @Query("""
            SELECT m.id, m.author_id, m.text, m.created_at
            FROM messages.messages m
            INNER JOIN messages.subscriptions s ON s.followee_id = m.author_id
            WHERE s.follower_id = :followerId
            ORDER BY m.created_at DESC, m.id DESC
            LIMIT :limit OFFSET :offset
            """)
    List<Message> findFeed(
            @Param("followerId") UUID followerId,
            @Param("limit") int limit,
            @Param("offset") int offset);
}
