package com.eric_eldard.portfolio.model.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
public class PortfolioUser implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "user",
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private List<LoginAttempt> loginAttempts;

    private int failedPasswordAttempts;

    private Date lockedOn;

    private Date authorizedUntil;

    private boolean enabled;

    private boolean admin;

    public PortfolioUser()
    {
        // framework ctor
    }

    public PortfolioUser(String username, String password, Date authorizedUntil, boolean enabled, boolean admin)
    {
        this.username = username;
        this.password = password;
        this.authorizedUntil = authorizedUntil;
        this.enabled = enabled;
        this.admin = admin;
    }

    @Transient
    public boolean isAccountLocked()
    {
        return lockedOn != null;
    }

    @Transient
    public Date getLastSuccessfulLoginTimestamp()
    {
        return loginAttempts.stream()
            .filter(attempt -> attempt.getOutcome() == LoginOutcome.SUCCESS)
            .map(LoginAttempt::getTimestamp)
            .max(Date::compareTo)
            .orElse(null);
    }

    @Transient
    public void incrementFailedPasswordAttempts()
    {
        failedPasswordAttempts++;
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if (admin)
        {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return null;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired()
    {
        return authorizedUntil == null || authorizedUntil.after(new Date());
    }

    @Override
    @Transient
    public boolean isAccountNonLocked()
    {
        return !isAccountLocked();
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /**
     * @return {@link #loginAttempts} sorted in descending {@link LoginAttempt#timestamp} order
     */
    @Transient
    public List<LoginAttempt> getLoginAttemptsSorted()
    {
        return loginAttempts.stream()
            .sorted(Comparator.comparing(LoginAttempt::getTimestamp).reversed())
            .toList();
    }
}