package com.eric_eldard.portfolio.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@Controller
@RequestMapping("/portfolio/users")
public class UsersPageController
{
    private final PortfolioUserService userService;

    public UsersPageController(PortfolioUserService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    public String getUserManagementPage(Model model)
    {
        List<PortfolioUser> allUsers = userService.findAll();
        model.addAttribute("userList", allUsers);
        return "admin/user-management";
    }

    @GetMapping("/{id}")
    public String getUserManagementPage(@PathVariable long id, Model model)
    {
        PortfolioUser user = userService.findById(id).
            orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        model.addAttribute("user", user);
        return "admin/view-user";
    }
}