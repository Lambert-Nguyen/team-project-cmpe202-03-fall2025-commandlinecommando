package com.commandlinecommandos.campusmarketplace.service;

import com.commandlinecommandos.campusmarketplace.model.LoginAttempt;
import com.commandlinecommandos.campusmarketplace.repository.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoginAttemptService
 */
@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {
    
    @Mock
    private LoginAttemptRepository loginAttemptRepository;
    
    @Mock
    private AuditService auditService;
    
    @InjectMocks
    private LoginAttemptService loginAttemptService;
    
    private String testUsername = "testuser";
    private String testIpAddress = "192.168.1.1";
    
    @BeforeEach
    void setUp() {
        // Clear request context
        RequestContextHolder.resetRequestAttributes();
    }
    
    @Test
    void testRecordSuccessfulLogin() {
        // Arrange
        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenReturn(new LoginAttempt());
        
        // Act
        loginAttemptService.recordSuccessfulLogin(testUsername);
        
        // Assert
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }
    
    @Test
    void testRecordFailedLogin() {
        // Arrange
        String failureReason = "Invalid credentials";
        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenReturn(new LoginAttempt());
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(any(), any())).thenReturn(3L);
        
        // Act
        loginAttemptService.recordFailedLogin(testUsername, failureReason);
        
        // Assert
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
        verify(auditService, times(1)).logFailedLogin(testUsername, failureReason);
    }
    
    @Test
    void testIsAccountLocked_NotLocked() {
        // Arrange
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(any(), any())).thenReturn(3L);
        
        // Act
        boolean isLocked = loginAttemptService.isAccountLocked(testUsername);
        
        // Assert
        assertFalse(isLocked);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(any(), any());
    }
    
    @Test
    void testIsAccountLocked_Locked() {
        // Arrange
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(any(), any())).thenReturn(5L);
        
        // Act
        boolean isLocked = loginAttemptService.isAccountLocked(testUsername);
        
        // Assert
        assertTrue(isLocked);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(any(), any());
    }
    
    @Test
    void testGetFailedAttemptCount() {
        // Arrange
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(any(), any())).thenReturn(3L);
        
        // Act
        int count = loginAttemptService.getFailedAttemptCount(testUsername);
        
        // Assert
        assertEquals(3, count);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(any(), any());
    }
    
    @Test
    void testResetFailedAttempts() {
        // Arrange
        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenReturn(new LoginAttempt());
        
        // Act
        loginAttemptService.resetFailedAttempts(testUsername);
        
        // Assert
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }
}

