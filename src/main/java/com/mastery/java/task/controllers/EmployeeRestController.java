package com.mastery.java.task.controllers;

import com.mastery.java.task.dto.Employee;
import com.mastery.java.task.exceptions.DuplicateEmployeeException;
import com.mastery.java.task.exceptions.InvalidDateException;
import com.mastery.java.task.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api("Employee rest")
public class EmployeeRestController {

    private static final Logger log = Logger.getLogger(EmployeeRestController.class);
    private static final Log logCon = LogFactory.getLog(EmployeeRestController.class);
    private final EmployeeService employeeService;
    private final JmsMessagingTemplate jmsMessagingTemplate;
    private final Queue queue;

    @Autowired
    public EmployeeRestController(EmployeeService employeeService, JmsMessagingTemplate jmsMessagingTemplate, Queue queue) {
        this.employeeService = employeeService;
        this.jmsMessagingTemplate = jmsMessagingTemplate;
        this.queue = queue;
    }

    @GetMapping("/getAll")
    @ApiOperation("method to get all employees")
    public ResponseEntity<List<Employee>> getAllEmployee() {
        List<Employee> employees = employeeService.getAll();
        log.trace("Getting all employees from the database");
        logCon.trace("Getting all employees from the database");
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/getById")
    @ApiOperation("method to get employee bu id")
    public ResponseEntity<Employee> getEmployeeById(@RequestParam("id") Integer id) {
        Employee employee = employeeService.getById(id);
        log.info("Employee with ID " + id + " received: " + employee);
        logCon.info("Employee with ID " + id + " received: " + employee);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/add")
    @ApiOperation("method to add employee")
    public ResponseEntity<?> addEmployee(@Valid Employee employee, BindingResult bindingResult) throws DuplicateEmployeeException, InvalidDateException {
        System.out.println(employee);
        if (bindingResult.hasErrors()) {
            log.info("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            log.trace("Invalid employee: " + bindingResult.getAllErrors());
            logCon.info("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            return ResponseEntity.ok(collectResponseToInvalidRequest(employee, bindingResult));
        }else {
            employeeService.save(employee);
            return ResponseEntity.ok(employee);
        }
    }

    @GetMapping("/update")
    @ApiOperation("method to update employee")
    public ResponseEntity<?> updateEmployee(@RequestParam("id") Integer id, @Valid Employee employee, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            log.debug("Invalid employee: " + bindingResult.getAllErrors());
            logCon.warn("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            return ResponseEntity.ok(collectResponseToInvalidRequest(employee, bindingResult));
        }
        employee.setEmployeeId(id);
        employeeService.update(employee);
        log.info("Employee with ID " + id + " changed: " + employee);
        logCon.info("Employee with ID " + id + " changed: " + employee);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/delete")
    @ApiOperation("method to delete employee by id")
    public ResponseEntity<Employee> deleteEmployee(@RequestParam("id") Integer id) {
        Employee employee = employeeService.getById(id);
        this.jmsMessagingTemplate.convertAndSend(this.queue, String.valueOf(id));
        log.info("Employee with ID " + id + " deleted");
        logCon.info("Employee with ID " + id + " deleted");
        return ResponseEntity.ok(employee);
    }

    public String collectResponseToInvalidRequest(Employee employee, BindingResult bindingResult) {
        String str = new String("Запрос имеет некорректные данные в след. полях :\n");
        for (Object object : bindingResult.getAllErrors()) {
            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;
                str += " Поле - " + fieldError.getField() + "; Ошибка - " + fieldError.getDefaultMessage() + ";\n";
            }
        }
        str += "Пожалуйста, откорректируйте запрос и повторите";
        return str;
    }
}