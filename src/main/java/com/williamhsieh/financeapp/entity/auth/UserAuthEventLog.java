package com.williamhsieh.financeapp.entity.auth;

import java.time.LocalDateTime;

import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.entity.user.UserRefreshToken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_auth_event_logs")
public class UserAuthEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_auth_event_type_id",
        nullable = false
    )
    private UserAuthEventType userAuthEventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id")
    private UserRefreshToken refreshToken;

    @Column(name = "is_success", nullable = false)
    private boolean success;

    @Column(name = "failure_detail", length = 255)
    private String failureDetail;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "session_id", length = 36)
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(
        name = "event_time",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime eventTime;

    protected UserAuthEventLog() {
    }

    public UserAuthEventLog(
        User user,
        UserAuthEventType userAuthEventType,
        UserRefreshToken refreshToken,
        boolean success,
        String failureDetail,
        String email,
        String sessionId,
        String ipAddress,
        String userAgent
    ) {
        this.user = user;
        this.userAuthEventType = userAuthEventType;
        this.refreshToken = refreshToken;
        this.success = success;
        this.failureDetail = failureDetail;
        this.email = email;
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public UserAuthEventType getUserAuthEventType() {
        return userAuthEventType;
    }

    public UserRefreshToken getRefreshToken() {
        return refreshToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureDetail() {
        return failureDetail;
    }

    public String getEmail() {
        return email;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }
}
