package com.portfolio.portfoliobuilder.service;

import com.portfolio.portfoliobuilder.dto.DashboardResponse;
import com.portfolio.portfoliobuilder.dto.TemplateOption;
import com.portfolio.portfoliobuilder.entity.User;
import com.portfolio.portfoliobuilder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private static final Map<String, String> TEMPLATE_OPTIONS = Map.of(
            "minimal", "Minimal",
            "modern", "Modern",
            "dark", "Dark Pro",
            "glass", "Glass",
            "bold", "Bold"
    );

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final CertificateRepository certificateRepository;
    private final InternshipRepository internshipRepository;

    public DashboardResponse getDashboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildResponse(user);
    }

    public DashboardResponse getPublicPortfolio(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildResponse(user);
    }

    public void updateTemplate(String username, String selectedTemplate) {
        if (selectedTemplate == null || !TEMPLATE_OPTIONS.containsKey(selectedTemplate)) {
            throw new IllegalArgumentException("Invalid template selected");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setSelectedTemplate(selectedTemplate);
        userRepository.save(user);
    }

    public List<TemplateOption> getAvailableTemplates() {
        return TEMPLATE_OPTIONS.entrySet().stream()
                .map(entry -> new TemplateOption(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private DashboardResponse buildResponse(User user) {
        DashboardResponse response = new DashboardResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setSelectedTemplate(user.getSelectedTemplate());
        response.setProjects(projectRepository.findByUserId(user.getId()));
        response.setSkills(skillRepository.findByUserId(user.getId()));
        response.setExperiences(experienceRepository.findByUserId(user.getId()));
        response.setCertificates(certificateRepository.findByUserId(user.getId()));
        response.setInternships(internshipRepository.findByUserId(user.getId()));
        return response;
    }
}
