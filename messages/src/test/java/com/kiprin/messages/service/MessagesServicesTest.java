package com.kiprin.messages.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.exception.BadRequestException;
import com.kiprin.common.exception.ForbiddenException;
import com.kiprin.common.exception.NotFoundException;
import com.kiprin.messages.client.UmsClient;
import com.kiprin.messages.domain.Message;
import com.kiprin.messages.domain.Subscription;
import com.kiprin.messages.dto.CreateMessageRequest;
import com.kiprin.messages.repository.MessageRepository;
import com.kiprin.messages.repository.SubscriptionRepository;

class MessagesServicesTest {

    @Test
    void producerCanCreateMessage() {
        MessageRepository repo = mock(MessageRepository.class);
        when(repo.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));
        MessageService service = new MessageService(repo);

        AuthPrincipal producer = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.PRODUCER));
        CreateMessageRequest req = new CreateMessageRequest();
        req.setText("  Hello WUPHF!  ");

        Message created = service.create(producer, req);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getAuthorId()).isEqualTo(producer.userId());
        assertThat(created.getText()).isEqualTo("Hello WUPHF!");
    }

    @Test
    void subscriberOnlyCannotCreateMessage() {
        MessageRepository repo = mock(MessageRepository.class);
        MessageService service = new MessageService(repo);

        AuthPrincipal subscriber = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.SUBSCRIBER));
        CreateMessageRequest req = new CreateMessageRequest();
        req.setText("Nope");

        assertThatThrownBy(() -> service.create(subscriber, req))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void nonAuthorNonAdminCannotDeleteMessage() {
        MessageRepository repo = mock(MessageRepository.class);
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);
        message.setAuthorId(UUID.randomUUID());
        when(repo.findById(messageId)).thenReturn(Optional.of(message));

        MessageService service = new MessageService(repo);
        AuthPrincipal other = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.PRODUCER));

        assertThatThrownBy(() -> service.delete(other, messageId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void adminCanDeleteOthersMessage() {
        MessageRepository repo = mock(MessageRepository.class);
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);
        message.setAuthorId(UUID.randomUUID());
        when(repo.findById(messageId)).thenReturn(Optional.of(message));

        MessageService service = new MessageService(repo);
        AuthPrincipal admin = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.ADMIN));

        service.delete(admin, messageId);
        verify(repo).deleteById(messageId);
    }

    @Test
    void subscribeChecksRoleSelfAndUmsUserExistence() {
        SubscriptionRepository subRepo = mock(SubscriptionRepository.class);
        UmsClient umsClient = mock(UmsClient.class);
        SubscriptionService service = new SubscriptionService(subRepo, umsClient);

        UUID userId = UUID.randomUUID();
        UUID followeeId = UUID.randomUUID();
        AuthPrincipal producerOnly = new AuthPrincipal(userId, Set.of(RoleName.PRODUCER));
        AuthPrincipal subscriber = new AuthPrincipal(userId, Set.of(RoleName.SUBSCRIBER));

        assertThatThrownBy(() -> service.subscribe(producerOnly, followeeId))
                .isInstanceOf(ForbiddenException.class);

        assertThatThrownBy(() -> service.subscribe(subscriber, userId))
                .isInstanceOf(BadRequestException.class);

        when(umsClient.userExists(followeeId)).thenReturn(false);
        assertThatThrownBy(() -> service.subscribe(subscriber, followeeId))
                .isInstanceOf(NotFoundException.class);

        when(umsClient.userExists(followeeId)).thenReturn(true);
        Subscription expected = new Subscription(userId, followeeId, LocalDateTime.now());
        when(subRepo.saveIfAbsent(any(), any(), any())).thenReturn(expected);

        assertThat(service.subscribe(subscriber, followeeId)).isEqualTo(expected);
    }

    @Test
    void feedRequiresSubscriberOrAdminAndValidBounds() {
        MessageRepository repo = mock(MessageRepository.class);
        FeedService service = new FeedService(repo);

        AuthPrincipal producerOnly = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.PRODUCER));
        assertThatThrownBy(() -> service.getFeed(producerOnly, 50, 0))
                .isInstanceOf(ForbiddenException.class);

        AuthPrincipal subscriber = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.SUBSCRIBER));
        assertThatThrownBy(() -> service.getFeed(subscriber, 0, 0))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> service.getFeed(subscriber, 10, -1))
                .isInstanceOf(BadRequestException.class);

        when(repo.findFeed(subscriber.userId(), 20, 0)).thenReturn(List.of());
        assertThat(service.getFeed(subscriber, 20, 0)).isEmpty();
    }
}
