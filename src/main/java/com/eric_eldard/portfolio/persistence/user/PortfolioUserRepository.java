package com.eric_eldard.portfolio.persistence.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.eric_eldard.portfolio.model.user.PortfolioUser;

@Repository
public interface PortfolioUserRepository extends JpaRepository<PortfolioUser, Long>
{
    @Query("FROM PortfolioUser")
    @EntityGraph(attributePaths = {"loginAttempts", "portfolioAuthorities"})
    List<PortfolioUser> findAllFullyHydrated();

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"loginAttempts", "portfolioAuthorities"})
    Optional<PortfolioUser> findFullyHydratedById(long id);

    @EntityGraph(attributePaths = {"portfolioAuthorities"})
    Optional<PortfolioUser> findWithAuthoritiesById(long id);

    @EntityGraph(attributePaths = {"loginAttempts", "portfolioAuthorities"})
    Optional<PortfolioUser> findFullyHydratedByUsername(String username);

    @EntityGraph(attributePaths = {"portfolioAuthorities"})
    Optional<PortfolioUser> findWithAuthoritiesByUsername(String username);
}