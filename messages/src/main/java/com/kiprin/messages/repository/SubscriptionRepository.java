package com.kiprin.messages.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.kiprin.messages.domain.Subscription;

@Repository
public class SubscriptionRepository {

    private static final RowMapper<Subscription> ROW_MAPPER = (rs, rowNum) -> {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Subscription(
                rs.getObject("follower_id", UUID.class),
                rs.getObject("followee_id", UUID.class),
                ts != null ? ts.toLocalDateTime() : null);
    };

    private final JdbcClient jdbcClient;

    public SubscriptionRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Subscription saveIfAbsent(UUID followerId, UUID followeeId, LocalDateTime createdAt) {
        jdbcClient.sql("""
                INSERT INTO messages.subscriptions (follower_id, followee_id, created_at)
                VALUES (:followerId, :followeeId, :createdAt)
                ON CONFLICT (follower_id, followee_id) DO NOTHING
                """)
                .param("followerId", followerId)
                .param("followeeId", followeeId)
                .param("createdAt", Timestamp.valueOf(createdAt))
                .update();

        return find(followerId, followeeId)
                .orElseThrow(() -> new IllegalStateException("Subscription not found after insert"));
    }

    public Optional<Subscription> find(UUID followerId, UUID followeeId) {
        return jdbcClient.sql("""
                SELECT follower_id, followee_id, created_at
                FROM messages.subscriptions
                WHERE follower_id = :followerId AND followee_id = :followeeId
                """)
                .param("followerId", followerId)
                .param("followeeId", followeeId)
                .query(ROW_MAPPER)
                .optional();
    }

    public int delete(UUID followerId, UUID followeeId) {
        return jdbcClient.sql("""
                DELETE FROM messages.subscriptions
                WHERE follower_id = :followerId AND followee_id = :followeeId
                """)
                .param("followerId", followerId)
                .param("followeeId", followeeId)
                .update();
    }

    public List<Subscription> findFollowing(UUID followerId) {
        return jdbcClient.sql("""
                SELECT follower_id, followee_id, created_at
                FROM messages.subscriptions
                WHERE follower_id = :followerId
                ORDER BY created_at DESC, followee_id ASC
                """)
                .param("followerId", followerId)
                .query(ROW_MAPPER)
                .list();
    }

    public List<Subscription> findFollowers(UUID followeeId) {
        return jdbcClient.sql("""
                SELECT follower_id, followee_id, created_at
                FROM messages.subscriptions
                WHERE followee_id = :followeeId
                ORDER BY created_at DESC, follower_id ASC
                """)
                .param("followeeId", followeeId)
                .query(ROW_MAPPER)
                .list();
    }
}
