package com.eric_eldard.portfolio.persistence.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eric_eldard.portfolio.model.user.PortfolioUser;

import java.util.Optional;

@Repository
public interface PortfolioUserRepository extends JpaRepository<PortfolioUser, Long>
{
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = "loginAttempts")
    Optional<PortfolioUser> findByUsername(String username);
}