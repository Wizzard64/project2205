package com.mastery.java.task.dao;

import com.mastery.java.task.dto.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Integer> {


    @Query(value = "SELECT employee_id FROM employee WHERE first_name = :firstName AND last_name = :lastName AND departament_id = :departamentId " +
            "AND job_title = :jobTitle AND gender = :gender AND date_of_birth = :dateOfBirth", nativeQuery = true)
    public Integer getExistingEmployee(@Param("firstName") String firstName,
                                       @Param("lastName") String lastName,
                                       @Param("departamentId") Integer departamentId,
                                       @Param("jobTitle") String jobTitle,
                                       @Param("gender") String gender,
                                       @Param("dateOfBirth") LocalDate dateOfBirth);
}
