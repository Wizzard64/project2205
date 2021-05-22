package com.mastery.java.task.service;


import com.mastery.java.task.dao.EmployeeDao;
import com.mastery.java.task.dto.Employee;
import com.mastery.java.task.dto.Gender;
import com.mastery.java.task.exceptions.DuplicateEmployeeException;
import com.mastery.java.task.exceptions.InvalidDateException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mastery.java.task.dto.Gender.FEMALE;
import static com.mastery.java.task.dto.Gender.MALE;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;


public class EmployeeServiceTest {

    private List<Employee> employeeList;
    private final int employeeId = 1;
    private final String firstName = "Andy";
    private final String lastName = "Samberg";
    private final Integer departamentId = 1;
    private final String jobTitle = "Java Developer";
    private final String gender = "FEMALE";
    private final LocalDate date = LocalDate.of(1998, 02, 02);

    @Mock
    EmployeeDao employeeDao;
    @InjectMocks
    EmployeeService employeeService;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveTest() throws InvalidDateException, DuplicateEmployeeException {
        Employee emp = stubEmployee();
        when(employeeDao.getExistingEmployee(emp.getFirstName(),
                emp.getLastName(), emp.getDepartamentId(), emp.getJobTitle(), emp.getGender(), emp.getDateOfBirth())).thenReturn(null);
        employeeService.save(emp);
        then(employeeDao).should().save(emp);
    }

    @Test(expected = InvalidDateException.class)
    public void saveTestWithInvalidDateException() throws InvalidDateException, DuplicateEmployeeException {
        Employee emp = stubEmployee();
        emp.setDateOfBirth(LocalDate.of(2030, 02, 02));
        when(employeeDao.getExistingEmployee(emp.getFirstName(),
                emp.getLastName(), emp.getDepartamentId(), emp.getJobTitle(), emp.getGender(), emp.getDateOfBirth())).thenReturn(null);
        employeeService.save(emp);
        then(employeeDao).should().save(emp);
    }

    @Test(expected = DuplicateEmployeeException.class)
    public void saveTestWithDuplicateEmployeeException() throws InvalidDateException, DuplicateEmployeeException {
        Employee emp = stubEmployee();
        when(employeeDao.getExistingEmployee(emp.getFirstName(),
                emp.getLastName(), emp.getDepartamentId(), emp.getJobTitle(), emp.getGender(), emp.getDateOfBirth())).thenReturn(1);
        employeeService.save(emp);
        then(employeeDao).should().save(emp);
    }

    @Test
    public void getByIdTest() {
        Employee emp = stubEmployee();

        when(employeeDao.getEmployeeByEmployeeId(employeeId)).thenReturn(emp);

        Employee employee = employeeService.getById(employeeId);

        assertEquals(emp.getEmployeeId(), employee.getEmployeeId());
        assertEquals(emp.getFirstName(), employee.getFirstName());
        assertEquals(emp.getLastName(), employee.getLastName());
        assertEquals(emp.getDepartamentId(), employee.getDepartamentId());
        assertEquals(emp.getGender(), employee.getGender());
        assertEquals(emp.getDateOfBirth(), employee.getDateOfBirth());

        then(employeeDao).should().getEmployeeByEmployeeId(employeeId);
    }

    @Test
    public void updateTest() {

        Employee emp = stubEmployee();
        employeeService.update(emp);

        then(employeeDao).should().save(emp);
    }

    @Test
    public void deleteByIdTest() {
        employeeService.deleteById(employeeId);
        then(employeeDao).should().deleteById(employeeId);
    }

    @Test
    public void getAllTest() {
        List<Employee> employeeList = stubEmployeeList();

        when(employeeDao.findAll(Sort.by(Sort.Direction.ASC, "employeeId"))).thenReturn(employeeList);

        List<Employee> empList = employeeService.getAll();

        assertEquals(3, empList.size());
        then(employeeDao).should().findAll(Sort.by(Sort.Direction.ASC, "employeeId"));
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
            Employee emp = new Employee();
            emp.setEmployeeId(employeeId[i]);
            emp.setFirstName(firstName[i]);
            emp.setLastName(lastName[i]);
            emp.setDepartamentId(departamentId[i]);
            emp.setJobTitle(jobTitle[i]);
            emp.setGender(gender[i].toString());
            emp.setDateOfBirth(date);
            employeeList.add(emp);
        }
        return employeeList;
    }

    private Employee stubEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDepartamentId(departamentId);
        employee.setJobTitle(jobTitle);
        employee.setGender(gender);
        employee.setDateOfBirth(date);
        return employee;
    }
}
