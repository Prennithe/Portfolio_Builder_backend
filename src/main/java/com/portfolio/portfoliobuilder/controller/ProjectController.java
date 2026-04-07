package com.portfolio.portfoliobuilder.controller;

import com.portfolio.portfoliobuilder.entity.Project;
import com.portfolio.portfoliobuilder.entity.User;
import com.portfolio.portfoliobuilder.repository.ProjectRepository;
import com.portfolio.portfoliobuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public List<Project> getAll(@AuthenticationPrincipal UserDetails ud) {
        return projectRepository.findByUserId(getUser(ud).getId());
    }

    @PostMapping
    public Project create(@AuthenticationPrincipal UserDetails ud, @RequestBody Project project) {
        project.setUser(getUser(ud));
        return projectRepository.save(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@AuthenticationPrincipal UserDetails ud,
                                          @PathVariable Long id, @RequestBody Project updated) {
        return projectRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(getUser(ud).getId()))
                .map(p -> {
                    p.setTitle(updated.getTitle());
                    p.setDescription(updated.getDescription());
                    p.setGithubLink(updated.getGithubLink());
                    p.setLiveLink(updated.getLiveLink());
                    return ResponseEntity.ok(projectRepository.save(p));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return projectRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(getUser(ud).getId()))
                .map(p -> { projectRepository.delete(p); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }
}
