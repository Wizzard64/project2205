package com.mastery.java.task.dao;

import com.mastery.java.task.dto.Employee;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
class EmployeeDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeDao employeeDao;

    private final int employeeId = 1;
    private final String firstName = "Andy";
    private final String lastName = "Samberg";
    private final int departamentId = 1;
    private final String jobTitle = "Java Developer";
    private final String gender = "MALE";
    private final LocalDate date = LocalDate.of(1989, 02, 02);

    @Test
    public void getExistingEmployeeTest() {
        Employee employee = stubEmployee();
        entityManager.merge(employee);
        entityManager.flush();
        Integer idEmp = employeeDao.getExistingEmployee(employee.getFirstName(), employee.getLastName(),
                employee.getDepartamentId(), employee.getJobTitle(),
                employee.getGender(), employee.getDateOfBirth());
        assertThat(idEmp).isEqualTo(employeeDao.findById(idEmp).get().getEmployeeId());
    }

    private Employee stubEmployee() {
        Employee employee = Employee.builder()
                .employeeId(employeeId)
                .firstName(firstName)
                .lastName(lastName)
                .departamentId(departamentId)
                .jobTitle(jobTitle)
                .gender(gender)
                .dateOfBirth(date).build();
        return employee;
    }
}
