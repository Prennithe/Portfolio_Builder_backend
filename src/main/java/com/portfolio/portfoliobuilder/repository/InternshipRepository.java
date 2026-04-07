package com.portfolio.portfoliobuilder.repository;

import com.portfolio.portfoliobuilder.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByUserId(Long userId);
}
