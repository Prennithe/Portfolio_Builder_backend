package com.portfolio.portfoliobuilder.controller;

import com.portfolio.portfoliobuilder.dto.*;
import com.portfolio.portfoliobuilder.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/{username}")
    public ResponseEntity<DashboardResponse> getPublicPortfolio(@PathVariable String username) {
        return ResponseEntity.ok(portfolioService.getPublicPortfolio(username));
    }

    @PutMapping("/template")
    public ResponseEntity<String> updateTemplate(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody TemplateRequest request) {
        portfolioService.updateTemplate(userDetails.getUsername(), request.getSelectedTemplate());
        return ResponseEntity.ok("Template updated successfully");
    }

    @GetMapping("/templates")
    public ResponseEntity<List<TemplateOption>> getTemplateOptions() {
        return ResponseEntity.ok(portfolioService.getAvailableTemplates());
    }
}
