package com.college.bonafide;

import com.college.bonafide.model.Role;
import com.college.bonafide.model.Student;
import com.college.bonafide.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BonafideApplication {

    public static void main(String[] args) {
        SpringApplication.run(BonafideApplication.class, args);
    }

    /**
     * Seeds a default admin account and a demo student account on startup
     * so the app is usable immediately without a separate registration step.
     */
    @Bean
    public CommandLineRunner seedData(StudentRepository studentRepository) {
        return args -> {
            if (studentRepository.findByEmail("admin@college.edu").isEmpty()) {
                Student admin = new Student();
                admin.setName("College Admin");
                admin.setEmail("admin@college.edu");
                admin.setPassword("admin123");
                admin.setRole(Role.ADMIN);
                admin.setRollNumber("ADMIN000");
                admin.setDepartment("Administration");
                admin.setYear("-");
                studentRepository.save(admin);
            }

            if (studentRepository.findByEmail("student@college.edu").isEmpty()) {
                Student student = new Student();
                student.setName("Demo Student");
                student.setEmail("student@college.edu");
                student.setPassword("student123");
                student.setRole(Role.STUDENT);
                student.setRollNumber("CS2023001");
                student.setDepartment("Computer Science");
                student.setYear("3rd Year");
                studentRepository.save(student);
            }
        };
    }
}
