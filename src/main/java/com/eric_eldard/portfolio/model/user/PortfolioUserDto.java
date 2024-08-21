package com.eric_eldard.portfolio.model.user;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Date;

import com.eric_eldard.portfolio.util.Constants;

@Getter @Setter
public class PortfolioUserDto
{
    @Pattern(regexp = "^[A-Za-z0-9\\-_+@.]+$")
    private String username;

    @NotBlank
    @Size(min = Constants.MIN_PASSWORD_CHARS)
    private String password;

    private Date authorizedUntil;

    private boolean enabled;

    private boolean admin;
}