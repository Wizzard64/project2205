package com.mastery.java.task.service;

import com.mastery.java.task.dao.EmployeeDao;
import com.mastery.java.task.dto.Employee;
import com.mastery.java.task.exceptions.DuplicateEmployeeException;
import com.mastery.java.task.exceptions.InvalidDateException;
import org.springframework.data.domain.Sort;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    private final EmployeeDao employeeDao;

    public void save(Employee employee) throws DuplicateEmployeeException, InvalidDateException {
        if (getExistingEmployeeId(employee) != null) {
            throw new DuplicateEmployeeException(employee, getExistingEmployeeId(employee), "Ошибка: Дублирование пользователя. Пользователь с такими данными существует - id " + getExistingEmployeeId(employee));
        }else {
            if(employee.getDateOfBirth().isAfter(LocalDate.now())){
                throw new InvalidDateException("Ошибка: Дата из будущего, введите корректную дату");
            }else {
                employeeDao.save(employee);
            }
        }
    }

    private Integer getExistingEmployeeId(Employee employee){
        return employeeDao.getExistingEmployee(employee.getFirstName(),
                employee.getLastName(), employee.getDepartamentId(), employee.getJobTitle(), employee.getGender(), employee.getDateOfBirth());
    }

    public Employee getById(int id) {
        return employeeDao.getEmployeeByEmployeeId(id);
    }


    public void update(Employee employee) {
        employeeDao.save(employee);
    }


    @JmsListener(destination = "simplewebapp.queue")
    public void deleteById(int id) {
        employeeDao.deleteById(id);
    }


    public List<Employee> getAll() {
        return employeeDao.findAll(Sort.by(Sort.Direction.ASC, "employeeId"));
    }
}
