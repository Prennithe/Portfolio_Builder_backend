package com.portfolio.portfoliobuilder.controller;

import com.portfolio.portfoliobuilder.entity.Internship;
import com.portfolio.portfoliobuilder.entity.User;
import com.portfolio.portfoliobuilder.repository.InternshipRepository;
import com.portfolio.portfoliobuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public List<Internship> getAll(@AuthenticationPrincipal UserDetails ud) {
        return internshipRepository.findByUserId(getUser(ud).getId());
    }

    @PostMapping
    public Internship create(@AuthenticationPrincipal UserDetails ud, @RequestBody Internship internship) {
        internship.setUser(getUser(ud));
        return internshipRepository.save(internship);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Internship> update(@AuthenticationPrincipal UserDetails ud,
                                             @PathVariable Long id, @RequestBody Internship updated) {
        return internshipRepository.findById(id)
                .filter(i -> i.getUser().getId().equals(getUser(ud).getId()))
                .map(i -> {
                    i.setCompany(updated.getCompany());
                    i.setRole(updated.getRole());
                    i.setDescription(updated.getDescription());
                    i.setCertificateUrl(updated.getCertificateUrl());
                    return ResponseEntity.ok(internshipRepository.save(i));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return internshipRepository.findById(id)
                .filter(i -> i.getUser().getId().equals(getUser(ud).getId()))
                .map(i -> { internshipRepository.delete(i); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }
}
