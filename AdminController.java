package com.college.bonafide.controller;

import com.college.bonafide.model.BonafideRequest;
import com.college.bonafide.model.RequestStatus;
import com.college.bonafide.model.Role;
import com.college.bonafide.model.Student;
import com.college.bonafide.repository.BonafideRequestRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BonafideRequestRepository requestRepository;

    public AdminController(BonafideRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    private Student requireAdmin(HttpSession session) {
        Student user = (Student) session.getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            return null;
        }
        return user;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student admin = requireAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin", admin);
        model.addAttribute("requests", requestRepository.findAllByOrderByRequestedAtDesc());
        return "admin-dashboard";
    }

    @PostMapping("/request/{id}/approve")
    public String approve(@PathVariable Long id,
                           @RequestParam(required = false) String remarks,
                           HttpSession session) {
        Student admin = requireAdmin(session);
        if (admin == null) return "redirect:/login";

        BonafideRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
            request.setStatus(RequestStatus.APPROVED);
            request.setAdminRemarks(remarks);
            request.setProcessedAt(LocalDateTime.now());
            requestRepository.save(request);
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/request/{id}/reject")
    public String reject(@PathVariable Long id,
                          @RequestParam(required = false) String remarks,
                          HttpSession session) {
        Student admin = requireAdmin(session);
        if (admin == null) return "redirect:/login";

        BonafideRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
            request.setStatus(RequestStatus.REJECTED);
            request.setAdminRemarks(remarks);
            request.setProcessedAt(LocalDateTime.now());
            requestRepository.save(request);
        }
        return "redirect:/admin/dashboard";
    }
}
