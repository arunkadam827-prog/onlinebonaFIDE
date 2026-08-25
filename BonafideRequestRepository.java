package com.college.bonafide.repository;

import com.college.bonafide.model.BonafideRequest;
import com.college.bonafide.model.RequestStatus;
import com.college.bonafide.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonafideRequestRepository extends JpaRepository<BonafideRequest, Long> {
    List<BonafideRequest> findByStudentOrderByRequestedAtDesc(Student student);
    List<BonafideRequest> findByStatusOrderByRequestedAtDesc(RequestStatus status);
    List<BonafideRequest> findAllByOrderByRequestedAtDesc();
}
