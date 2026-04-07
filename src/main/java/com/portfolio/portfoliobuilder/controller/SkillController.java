package com.portfolio.portfoliobuilder.controller;

import com.portfolio.portfoliobuilder.entity.Skill;
import com.portfolio.portfoliobuilder.entity.User;
import com.portfolio.portfoliobuilder.repository.SkillRepository;
import com.portfolio.portfoliobuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public List<Skill> getAll(@AuthenticationPrincipal UserDetails ud) {
        return skillRepository.findByUserId(getUser(ud).getId());
    }

    @PostMapping
    public Skill create(@AuthenticationPrincipal UserDetails ud, @RequestBody Skill skill) {
        skill.setUser(getUser(ud));
        return skillRepository.save(skill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Skill> update(@AuthenticationPrincipal UserDetails ud,
                                        @PathVariable Long id, @RequestBody Skill updated) {
        return skillRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(getUser(ud).getId()))
                .map(s -> {
                    s.setName(updated.getName());
                    s.setLevel(updated.getLevel());
                    return ResponseEntity.ok(skillRepository.save(s));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return skillRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(getUser(ud).getId()))
                .map(s -> { skillRepository.delete(s); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }
}
