package ru.t2.employeeawards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t2.employeeawards.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    /**
     * Поиск сотрудника по внешнему идентификатору
     * @param employeeExternalId внешний идентификатор сотрудника
     * @return сотрудник
     */
    Optional<Employee> findByEmployeeExternalId(Long employeeExternalId);
}
