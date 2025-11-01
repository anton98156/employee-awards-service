package ru.t2.employeeawards.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "awards")
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id")
    private Long awardId;

    @NotBlank(message = "Award name is required")
    @Column(name = "award_name")
    String awardName;

    @Column(name = "received_date")
    LocalDateTime receivedDate;

    @Column(name = "award_external_id", unique = true)
    private Long awardExternalId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
