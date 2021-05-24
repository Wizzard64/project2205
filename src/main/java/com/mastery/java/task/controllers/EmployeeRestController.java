package com.mastery.java.task.controllers;

import com.mastery.java.task.dto.Employee;
import com.mastery.java.task.exceptions.DuplicateEmployeeException;
import com.mastery.java.task.exceptions.InvalidDateException;
import com.mastery.java.task.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api("Employee rest")
@AllArgsConstructor
public class EmployeeRestController {

    private static final Log logCon = LogFactory.getLog(EmployeeRestController.class);
    private final EmployeeService employeeService;
    private final JmsMessagingTemplate jmsMessagingTemplate;
    private final Queue queue;

    @GetMapping("/employees")
    @ApiOperation("method to get all employees")
    public ResponseEntity<List<Employee>> getAllEmployee() {
        List<Employee> employees = employeeService.getAll();
        logCon.trace("Getting all employees from the database");
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employee/{id}")
    @ApiOperation("method to get employee by id")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") Integer id) {
        Employee employee = employeeService.getById(id);
        logCon.info("Employee with ID " + id + " received: " + employee);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/employee")
    @ApiOperation("method to add employee")
    public ResponseEntity<?> addEmployee(@Valid Employee employee, BindingResult bindingResult) throws DuplicateEmployeeException, InvalidDateException {
        System.out.println(employee);
        if (bindingResult.hasErrors()) {
            logCon.info("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            return ResponseEntity.ok(collectResponseToInvalidRequest(employee, bindingResult));
        } else {
            employeeService.save(employee);
            return ResponseEntity.ok(employee);
        }
    }

    @PutMapping("/employee/{id}")
    @ApiOperation("method to update employee")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") Integer id, @Valid Employee employee, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logCon.warn("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            return ResponseEntity.ok(collectResponseToInvalidRequest(employee, bindingResult));
        }
        employee.setEmployeeId(id);
        employeeService.update(employee);
        logCon.info("Employee with ID " + id + " changed: " + employee);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/employee/{id}")
    @ApiOperation("method to delete employee by id")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable("id") Integer id) {
        Employee employee = employeeService.getById(id);
        this.jmsMessagingTemplate.convertAndSend(this.queue, String.valueOf(id));
        logCon.info("Employee with ID " + id + " deleted");
        return ResponseEntity.ok(employee);
    }

    public String collectResponseToInvalidRequest(Employee employee, BindingResult bindingResult) {
        String responseToInvalidRequest = new String("The request contains invalid data in the following fields :\n");
        for (Object object : bindingResult.getAllErrors()) {
            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;
                responseToInvalidRequest += " Field - " + fieldError.getField() + "; Error - " + fieldError.getDefaultMessage() + ";\n";
            }
        }
        responseToInvalidRequest += "Please correct the request and repeat";
        return responseToInvalidRequest;
    }
}