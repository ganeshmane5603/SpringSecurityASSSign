package com.example.employeeManagement.service;

import com.example.employeeManagement.entity.Employee;
import com.example.employeeManagement.repository.EmployeeRepo;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepo repo;
    public EmployeeService(EmployeeRepo repo) {
        this.repo = repo;
    }

    public Employee create(Employee e) {
        return repo.save(e); }

    public Optional<Employee> findById(Long id) {
        return repo.findById(id);
    }

    public List<Employee> findAll() {
        return repo.findAll();
    }
    public Employee update(Employee e) {
        return repo.save(e);
    }
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Page<Employee> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repo.findAll(pageable);
    }
}

