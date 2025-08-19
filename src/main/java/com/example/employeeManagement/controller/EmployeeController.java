package com.example.employeeManagement.controller;

import com.example.employeeManagement.entity.Employee;
import com.example.employeeManagement.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        Employee saved = service.create(employee);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        Optional<Employee> e = service.findById(id);
        return e.map(employee -> ResponseEntity.ok((Object) employee))
           .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found"));
    }

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        List<Employee> e = service.findAll();
        return e.map(employee -> ResponseEntity.ok((Object) employee))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Employee employee) {
        Optional<Employee> existing = service.findById(id);
        if (existing.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        Employee ex = existing.get();
        ex.setName(employee.getName());
        ex.setEmail(employee.getEmail());
        ex.setDepartment(employee.getDepartment());
        ex.setPosition(employee.getPosition());
        ex.setSalary(employee.getSalary());
        ex.setDateOfJoining(employee.getDateOfJoining());
        Employee updated = service.update(ex);
        return ResponseEntity.ok(updated);
    }

    // only admin can delete (example of authorization)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping
    public ResponseEntity<Page<Employee>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<Employee> employees = service.findAll(page, size, sortBy, sortDir);
        return ResponseEntity.ok(employees);
    }
}

