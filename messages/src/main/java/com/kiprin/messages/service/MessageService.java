package com.kiprin.messages.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.exception.BadRequestException;
import com.kiprin.common.exception.ForbiddenException;
import com.kiprin.common.exception.NotFoundException;
import com.kiprin.messages.domain.Message;
import com.kiprin.messages.dto.CreateMessageRequest;
import com.kiprin.messages.repository.MessageRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Message create(AuthPrincipal actor, CreateMessageRequest request) {
        if (!actor.hasAnyRole(RoleName.PRODUCER, RoleName.ADMIN)) {
            throw new ForbiddenException("PRODUCER or ADMIN role required");
        }
        String text = request.getText() == null ? "" : request.getText().trim();
        if (text.isEmpty()) {
            throw new BadRequestException("Message text must not be blank");
        }
        if (text.length() > 280) {
            throw new BadRequestException("Message text must be at most 280 characters");
        }

        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setAuthorId(actor.userId());
        message.setText(text);
        message.setCreatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public Message getById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
    }

    public List<Message> findByAuthor(UUID authorId) {
        return messageRepository.findByAuthorIdOrderByCreatedAtDescIdDesc(authorId);
    }

    @Transactional
    public void delete(AuthPrincipal actor, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        if (!actor.isAdmin() && !actor.userId().equals(message.getAuthorId())) {
            throw new ForbiddenException("Only author or ADMIN can delete message");
        }
        messageRepository.deleteById(messageId);
    }
}
