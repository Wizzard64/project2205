package com.mastery.java.task.dto;

import com.mastery.java.task.validator.ValidGender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Builder
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "data model of employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Integer employeeId;
    @NotBlank(message = "it should not be empty")
    @Size(min = 3, message = "must consist of at least 3 characters")
    private String firstName;
    @NotBlank(message = "it should not be empty")
    @Size(min = 3, message = "must consist of at least 3 characters")
    private String lastName;
    @NotNull(message = "it should not be empty")
    @Digits(integer = 2, fraction = 0)
    private Integer departamentId;
    @NotBlank(message = "it should not be empty")
    private String jobTitle;
    @NotNull(message = "it should not be empty")
    @ValidGender
    private String gender;
    @NotNull(message = "it should not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiParam("format: yyyy-MM-dd")
    private LocalDate dateOfBirth;
}
