package com.portfolio.portfoliobuilder.controller;

import com.portfolio.portfoliobuilder.entity.Certificate;
import com.portfolio.portfoliobuilder.entity.User;
import com.portfolio.portfoliobuilder.repository.CertificateRepository;
import com.portfolio.portfoliobuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public List<Certificate> getAll(@AuthenticationPrincipal UserDetails ud) {
        return certificateRepository.findByUserId(getUser(ud).getId());
    }

    @PostMapping
    public Certificate create(@AuthenticationPrincipal UserDetails ud, @RequestBody Certificate cert) {
        cert.setUser(getUser(ud));
        return certificateRepository.save(cert);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Certificate> update(@AuthenticationPrincipal UserDetails ud,
                                              @PathVariable Long id, @RequestBody Certificate updated) {
        return certificateRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(getUser(ud).getId()))
                .map(c -> {
                    c.setTitle(updated.getTitle());
                    c.setIssuer(updated.getIssuer());
                    c.setDescription(updated.getDescription());
                    c.setFileUrl(updated.getFileUrl());
                    return ResponseEntity.ok(certificateRepository.save(c));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return certificateRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(getUser(ud).getId()))
                .map(c -> { certificateRepository.delete(c); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }
}
