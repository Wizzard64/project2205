package com.mastery.java.task.controllers;

import com.mastery.java.task.dto.Employee;
import com.mastery.java.task.exceptions.DuplicateEmployeeException;
import com.mastery.java.task.exceptions.InvalidDateException;
import com.mastery.java.task.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import okhttp3.RequestBody;
import okhttp3.*;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.log4j.Level;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private static final Log logCon = LogFactory.getLog(EmployeeController.class);
    private String logLevel = "INFO";
    private final JmsMessagingTemplate jmsMessagingTemplate;
    private final Queue queue;

    @ModelAttribute("loglevel")
    public String addAttributeLogLevel() {
        return logLevel;
    }

    @ModelAttribute("employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAll();
    }

    @GetMapping(value = "/employees")
    public String home() {
        logCon.trace("Getting all employees from the database");
        logCon.info("Go to the main page");
        return "home";
    }

    @GetMapping("/employee")
    public String employeeAdd(Model model) {
        logCon.trace("Go to the page for adding an employee");
        model.addAttribute("employee", new Employee());
        return "employee-add";
    }

    @PostMapping("/employee")
    public String employeeAdd(@Valid Employee employee, BindingResult bindingResult, Model model) throws DuplicateEmployeeException, InvalidDateException {
        if (bindingResult.hasErrors()) {
            logCon.info("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            model.addAttribute("employee", employee);
            return "employee-add";
        }
        logCon.info("Employee added: " + employee);
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}")
    public String employeeDetails(@PathVariable(value = "id") int id, Model model) {
        Employee employee = employeeService.getById(id);
        logCon.info("Employee with ID " + id + " received: " + employee);
        model.addAttribute("employee", employee);
        return "employee-details";
    }

    @DeleteMapping("/employees/{id}")
    public String employeeDelete(@PathVariable(value = "id") int id) throws InterruptedException {
        this.jmsMessagingTemplate.convertAndSend(this.queue, String.valueOf(id));
        logCon.info("Employee with ID " + id + " deleted");
        Thread.sleep(50);
        return "redirect:/employees";
    }

    @PatchMapping("/employees/{id}")
    public String employeeEdit(@PathVariable(value = "id") int id, Model model) {
        Employee employee = employeeService.getById(id);
        logCon.trace("Go to the page for change an employee with id " + id);
        model.addAttribute("employee", employee);
        return "employee-edit";
    }

    @PutMapping("/employees/{id}")
    public String employeeUpdate(@PathVariable(value = "id") int id,
                                 @Valid Employee employee, BindingResult bindingResult, Model model) {
        employee.setEmployeeId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", employee);
            logCon.warn("Invalid employee has " + bindingResult.getFieldErrorCount() + " error");
            return "employee-edit";
        }
        employeeService.update(employee);
        logCon.info("Employee with ID " + id + " changed: " + employee);
        return "redirect:/employees";
    }

    @PostMapping("/lvl/{level}")
    public String changeLogLevel(@PathVariable(value = "level") String level) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"configuredLevel\": \"" + level + "\"}");
        Request request = new Request.Builder()
                .url("http://localhost:8080/actuator/loggers/com.mastery.java.task")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        logLevel = level;
        logCon.error("Logging level changed: " + level);
        return "redirect:/employees";
    }

    public Level getLevelLog4j(String logLevel) {
        switch (logLevel) {
            case "TRACE":
                return Level.TRACE;
            case "DEBUG":
                return Level.DEBUG;
            case "WARN":
                return Level.WARN;
            case "ERROR":
                return Level.ERROR;
            default:
                return Level.INFO;
        }
    }
}