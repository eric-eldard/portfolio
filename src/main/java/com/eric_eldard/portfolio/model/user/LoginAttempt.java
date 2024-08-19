package com.eric_eldard.portfolio.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Date;

@Entity
@Getter @Setter
public class LoginAttempt
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PortfolioUser user;

    @Enumerated(EnumType.STRING)
    private LoginOutcome outcome;

    @Nullable
    @Enumerated(EnumType.STRING)
    private LoginFailureReason failureReason;

    private Date timestamp;

    public static LoginAttempt makeSuccessfulAttempt(PortfolioUser user)
    {
        LoginAttempt successfulAttempt = new LoginAttempt();
        successfulAttempt.setUser(user);
        successfulAttempt.setOutcome(LoginOutcome.SUCCESS);
        successfulAttempt.setTimestamp(new Date());
        return successfulAttempt;
    }

    public static LoginAttempt makeFailedAttempt(PortfolioUser user, LoginFailureReason failureReason)
    {
        LoginAttempt failedAttempt = new LoginAttempt();
        failedAttempt.setUser(user);
        failedAttempt.setOutcome(LoginOutcome.FAILURE);
        failedAttempt.setFailureReason(failureReason);
        failedAttempt.setTimestamp(new Date());
        return failedAttempt;
    }
}