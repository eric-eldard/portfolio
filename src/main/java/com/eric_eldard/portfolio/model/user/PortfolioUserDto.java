package com.eric_eldard.portfolio.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Pattern;
import java.util.Date;

import com.eric_eldard.portfolio.validation.annotation.ValidPassword;

@Builder
@Getter @Setter
@NoArgsConstructor  // for frameworks
@AllArgsConstructor // for builder
public class PortfolioUserDto
{
    @Pattern(regexp = "^[A-Za-z0-9\\-_+@.]+$")
    private String username;

    @ValidPassword
    private String password;

    private Date authorizedUntil;

    private boolean enabled;

    private boolean admin;
}