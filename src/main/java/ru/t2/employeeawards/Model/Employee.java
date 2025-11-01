package ru.t2.employeeawards.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name")
    String fullName;

    @Column(name = "employee_external_id", unique = true)
    private Long employeeExternalId;
}
