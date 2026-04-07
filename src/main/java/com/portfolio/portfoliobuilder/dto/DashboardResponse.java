package com.portfolio.portfoliobuilder.dto;

import com.portfolio.portfoliobuilder.entity.*;
import lombok.Data;
import java.util.List;

@Data
public class DashboardResponse {
    private Long id;
    private String username;
    private String email;
    private String selectedTemplate;
    private List<Project> projects;
    private List<Skill> skills;
    private List<Experience> experiences;
    private List<Certificate> certificates;
    private List<Internship> internships;
}
