package com.college.bonafide.controller;

import com.college.bonafide.model.BonafideRequest;
import com.college.bonafide.model.RequestStatus;
import com.college.bonafide.model.Role;
import com.college.bonafide.model.Student;
import com.college.bonafide.repository.BonafideRequestRepository;
import com.college.bonafide.service.PdfCertificateService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final BonafideRequestRepository requestRepository;
    private final PdfCertificateService pdfCertificateService;

    public StudentController(BonafideRequestRepository requestRepository,
                              PdfCertificateService pdfCertificateService) {
        this.requestRepository = requestRepository;
        this.pdfCertificateService = pdfCertificateService;
    }

    private Student requireStudent(HttpSession session) {
        Student user = (Student) session.getAttribute("user");
        if (user == null || user.getRole() != Role.STUDENT) {
            return null;
        }
        return user;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = requireStudent(session);
        if (student == null) return "redirect:/login";

        model.addAttribute("student", student);
        model.addAttribute("requests", requestRepository.findByStudentOrderByRequestedAtDesc(student));
        return "student-dashboard";
    }

    @GetMapping("/request/new")
    public String newRequestForm(HttpSession session, Model model) {
        Student student = requireStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student", student);
        return "request-form";
    }

    @PostMapping("/request/new")
    public String submitRequest(@RequestParam String purpose,
                                 @RequestParam(required = false) String additionalDetails,
                                 HttpSession session) {
        Student student = requireStudent(session);
        if (student == null) return "redirect:/login";

        BonafideRequest request = new BonafideRequest();
        request.setStudent(student);
        request.setPurpose(purpose);
        request.setAdditionalDetails(additionalDetails);
        request.setStatus(RequestStatus.PENDING);
        requestRepository.save(request);

        return "redirect:/student/dashboard";
    }

    @GetMapping("/request/{id}/download")
    public void downloadCertificate(@PathVariable Long id,
                                     HttpSession session,
                                     HttpServletResponse response) throws IOException {
        Student student = requireStudent(session);
        if (student == null) {
            response.sendRedirect("/login");
            return;
        }

        BonafideRequest request = requestRepository.findById(id).orElse(null);
        if (request == null || !request.getStudent().getId().equals(student.getId())
                || request.getStatus() != RequestStatus.APPROVED) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            byte[] pdfBytes = pdfCertificateService.generateCertificate(request);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=bonafide_certificate_" + request.getId() + ".pdf");
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
