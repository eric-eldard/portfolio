package com.eric_eldard.portfolio.controller.admin;

import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/portfolio/users")
public class UsersRestController
{
    private final PortfolioUserService userService;

    @Inject
    public UsersRestController(PortfolioUserService userService)
    {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid PortfolioUserDto dto)
    {
        userService.create(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id)
    {
        userService.delete(id);
    }

    @PutMapping("/{id}/unlock")
    public void unlock(@PathVariable long id)
    {
        userService.unlock(id);
    }

    @PutMapping("/{id}/password")
    public void setPassword(@PathVariable long id, @RequestBody String password)
    {
        userService.setPassword(id, password);
    }

    @PutMapping("/{id}/authorized-until/{until}")
    public void setAuthorizedUntil(@PathVariable long id, @PathVariable String until)
    {
        if ("forever".equalsIgnoreCase(until))
        {
            userService.setInfiniteAuthorization(id);
        }
        else
        {
            try
            {
                userService.setAuthorizedUntil(id, new SimpleDateFormat("yyyy-MM-dd").parse(until));
            }
            catch (ParseException ex)
            {
                throw new IllegalArgumentException("[" + until + "] is not a valid date in the format yyyy-MM-dd");
            }
        }
    }

    @PutMapping("/{id}/toggle-enabled")
    public void toggleEnabled(@PathVariable long id)
    {
        userService.toggleEnabled(id);
    }

    @PutMapping("/{id}/toggle-admin")
    public void toggleRole(@PathVariable long id)
    {
        userService.toggleRole(id);
    }
}