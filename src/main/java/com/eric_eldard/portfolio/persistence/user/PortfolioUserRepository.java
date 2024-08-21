package com.eric_eldard.portfolio.persistence.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.eric_eldard.portfolio.model.user.PortfolioUser;

@Repository
public interface PortfolioUserRepository extends JpaRepository<PortfolioUser, Long>
{
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"loginAttempts", "portfolioAuthorities"})
    Optional<PortfolioUser> findByUsername(String username);
}