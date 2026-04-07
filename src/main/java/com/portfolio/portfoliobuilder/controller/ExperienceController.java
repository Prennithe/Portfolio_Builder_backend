package com.portfolio.portfoliobuilder.controller;

import com.portfolio.portfoliobuilder.entity.Experience;
import com.portfolio.portfoliobuilder.entity.User;
import com.portfolio.portfoliobuilder.repository.ExperienceRepository;
import com.portfolio.portfoliobuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public List<Experience> getAll(@AuthenticationPrincipal UserDetails ud) {
        return experienceRepository.findByUserId(getUser(ud).getId());
    }

    @PostMapping
    public Experience create(@AuthenticationPrincipal UserDetails ud, @RequestBody Experience exp) {
        exp.setUser(getUser(ud));
        return experienceRepository.save(exp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Experience> update(@AuthenticationPrincipal UserDetails ud,
                                             @PathVariable Long id, @RequestBody Experience updated) {
        return experienceRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(getUser(ud).getId()))
                .map(e -> {
                    e.setCompany(updated.getCompany());
                    e.setRole(updated.getRole());
                    e.setDescription(updated.getDescription());
                    return ResponseEntity.ok(experienceRepository.save(e));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return experienceRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(getUser(ud).getId()))
                .map(e -> { experienceRepository.delete(e); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }
}
