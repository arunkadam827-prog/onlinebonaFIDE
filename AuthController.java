package com.college.bonafide.controller;

import com.college.bonafide.model.Role;
import com.college.bonafide.model.Student;
import com.college.bonafide.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final StudentRepository studentRepository;

    public AuthController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        Student user = (Student) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return user.getRole() == Role.ADMIN ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {
        var studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty() || !studentOpt.get().getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
        Student student = studentOpt.get();
        session.setAttribute("user", student);
        return student.getRole() == Role.ADMIN ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam String rollNumber,
                            @RequestParam String department,
                            @RequestParam String year,
                            Model model) {
        if (studentRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setRollNumber(rollNumber);
        student.setDepartment(department);
        student.setYear(year);
        student.setRole(Role.STUDENT);
        studentRepository.save(student);

        model.addAttribute("success", "Registration successful. Please log in.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
