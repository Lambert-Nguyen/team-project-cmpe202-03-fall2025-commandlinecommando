package com.commandlinecommandos.campusmarketplace.communication.dto;

import com.commandlinecommandos.campusmarketplace.communication.model.Conversation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConversationResponse {
    
    private UUID conversationId;
    private UUID listingId;
    private UUID buyerId;
    private UUID sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageResponse> messages;
    private Long unreadCount;
    
    public ConversationResponse() {
    }
    
    public ConversationResponse(Conversation conversation) {
        this.conversationId = conversation.getConversationId();
        this.listingId = conversation.getListingId();
        this.buyerId = conversation.getBuyerId();
        this.sellerId = conversation.getSellerId();
        this.createdAt = conversation.getCreatedAt();
        this.updatedAt = conversation.getUpdatedAt();
        if (conversation.getMessages() != null) {
            this.messages = conversation.getMessages().stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
        }
    }
    
    public UUID getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }
    
    public UUID getListingId() {
        return listingId;
    }
    
    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }
    
    public UUID getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }
    
    public UUID getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<MessageResponse> getMessages() {
        return messages;
    }
    
    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }
    
    public Long getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
