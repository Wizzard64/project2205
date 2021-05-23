package com.mastery.java.task.controllers;

import com.mastery.java.task.dto.Employee;
import com.mastery.java.task.dto.Gender;
import com.mastery.java.task.service.EmployeeService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.jms.Queue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mastery.java.task.dto.Gender.FEMALE;
import static com.mastery.java.task.dto.Gender.MALE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @MockBean
    EmployeeService service;

    @MockBean
    JmsMessagingTemplate jmsMessagingTemplate;
    @MockBean
    Queue queue;

    @Autowired
    private MockMvc mockMvc;

    private List<Employee> employeeList;
    private final int employeeId = 1;
    private final String firstName = "Andy";
    private final String lastName = "Samberg";
    private final Integer departamentId = 1;
    private final String jobTitle = "Java Developer";
    private final String gender = "MALE";
    private final LocalDate date = LocalDate.of(1998, 02, 02);

    @Test
    public void addViewEmployeeTest() throws Exception {

        this.mockMvc.perform(get("/add")).andDo(print())
                .andExpect(status().isOk()).andExpect(view().name("employee-add"));
    }

    @Test
    public void addPostEmployeeTest() throws Exception {

        Employee employee = stubEmployeeWithoutId();

        this.mockMvc.perform(post("/add")
                .param("firstName", employee.getFirstName())
                .param("lastName", employee.getLastName())
                .param("departamentId", String.valueOf(employee.getDepartamentId()))
                .param("jobTitle", employee.getJobTitle())
                .param("gender", employee.getGender())
                .param("dateOfBirth", String.valueOf(employee.getDateOfBirth()))).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        assertEquals(firstName, employee.getFirstName());
        assertEquals(lastName, employee.getLastName());
        assertEquals(departamentId, employee.getDepartamentId());
        assertEquals(gender, employee.getGender());
        assertEquals(date, employee.getDateOfBirth());

        then(service).should().save(any(Employee.class));
    }


    @Test
    public void getAllEmployeeTest() throws Exception {
        employeeList = stubEmployeeList();
        when(service.getAll()).thenReturn(employeeList);
        this.mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().isOk()).andExpect(view().name("home"));
        then(service).should().getAll();
    }


    @Test
    public void getByIdEmployeeTest() throws Exception {
        Employee employee = stubEmployee();
        int id = employee.getEmployeeId();
        when(service.getById(id)).thenReturn(employee);
        this.mockMvc.perform(get("/get/" + id)).andDo(print())
                .andExpect(status().isOk()).andExpect(view().name("employee-details"));

        then(service).should().getById(id);
    }

    @Test
    public void updateEmployeeTest() throws Exception {
        Employee employee = stubEmployee();
        int id = employee.getEmployeeId();
        when(service.getById(id)).thenReturn(employee);
        this.mockMvc.perform(get("/" + id + "/edit")).andDo(print())
                .andExpect(status().isOk()).andExpect(view().name("employee-edit"));
        then(service).should().getById(id);
    }

    @Test
    public void updateEmployeePostTest() throws Exception {
        Employee employee = stubEmployee();
        int id = employee.getEmployeeId();
        when(service.getById(id)).thenReturn(employee);

        this.mockMvc.perform(post("/" + id + "/edit")
                .param("firstName", employee.getFirstName())
                .param("lastName", employee.getLastName())
                .param("departamentId", String.valueOf(employee.getDepartamentId()))
                .param("jobTitle", employee.getJobTitle())
                .param("gender", String.valueOf(employee.getGender()))
                .param("dateOfBirth", String.valueOf(employee.getDateOfBirth()))).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        then(service).should().update(employee);
    }

    @Test
    public void deleteEmployeeTest() throws Exception {
        Employee employee = stubEmployee();
        int id = employee.getEmployeeId();
        this.mockMvc.perform(post("/" + id + "/delete")).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        then(jmsMessagingTemplate).should().convertAndSend(queue, String.valueOf(id));
    }

    private List<Employee> stubEmployeeList() {
        List<Employee> employeeList = new ArrayList<Employee>();

        int[] employeeId = new int[]{1, 2, 3};
        String[] firstName = new String[]{"Andy", "Joe Lo", "Chelsea"};
        String[] lastName = new String[]{"Samberg", "Truglio", "Peretti"};
        int[] departamentId = new int[]{1, 2, 3};
        String[] jobTitle = new String[]{"Java Developer", "HR", "Tester"};
        Gender[] gender = new Gender[]{MALE, MALE, FEMALE};

        for (int i = 0; i < 3; i++) {
            Employee emp = Employee.builder()
                    .employeeId(employeeId[i])
                    .firstName(firstName[i])
                    .lastName(lastName[i])
                    .departamentId(departamentId[i])
                    .jobTitle(jobTitle[i])
                    .gender(String.valueOf(gender[i]))
                    .dateOfBirth(date).build();
            employeeList.add(emp);
        }
        return employeeList;
    }

    private Employee stubEmployee() {
        Employee employee = stubEmployeeWithoutId();
        employee.setEmployeeId(employeeId);
        return employee;
    }

    private Employee stubEmployeeWithoutId() {
        Employee employee = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .departamentId(departamentId)
                .jobTitle(jobTitle)
                .gender(gender)
                .dateOfBirth(date).build();
        return employee;
    }

}
