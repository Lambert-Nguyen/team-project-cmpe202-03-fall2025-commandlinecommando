package com.commandlinecommandos.campusmarketplace.communication.dto;

import com.commandlinecommandos.campusmarketplace.communication.model.Message;
import java.time.LocalDateTime;
import java.util.UUID;

public class MessageResponse {
    
    private UUID messageId;
    private UUID conversationId;
    private UUID senderId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
    
    public MessageResponse() {
    }
    
    public MessageResponse(Message message) {
        this.messageId = message.getMessageId();
        this.conversationId = message.getConversation().getConversationId();
        this.senderId = message.getSenderId();
        this.content = message.getContent();
        this.isRead = message.getIsRead();
        this.createdAt = message.getCreatedAt();
    }
    
    public UUID getMessageId() {
        return messageId;
    }
    
    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }
    
    public UUID getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }
    
    public UUID getSenderId() {
        return senderId;
    }
    
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
