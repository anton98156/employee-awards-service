package ru.t2.employeeawards.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardFileRecord {

    @NotNull
    private Long employeeExternalId;

    @NotBlank
    private String employeeFullName;

    @NotNull
    private Long awardExternalId;

    @NotBlank
    private String awardName;

    @NotNull
    private LocalDate receivedDate;
}
