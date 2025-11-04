package ru.t2.employeeawards.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.t2.employeeawards.model.Employee;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setFullName("Дмитрий Кузнецов");
        employee.setEmployeeExternalId(189L);
        entityManager.persistAndFlush(employee);
    }

    @Test
    void findByEmployeeExternalId_WhenEmployeeExists_ShouldReturnEmployee() {
        Optional<Employee> found = employeeRepository.findByEmployeeExternalId(189L);

        assertThat(found).isPresent();
        assertThat(found.get().getEmployeeExternalId()).isEqualTo(189L);
        assertThat(found.get().getFullName()).isEqualTo("Дмитрий Кузнецов");
    }

    @Test
    void findByEmployeeExternalId_WhenEmployeeNotExists_ShouldReturnEmpty() {
        Optional<Employee> found = employeeRepository.findByEmployeeExternalId(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistEmployee() {
        Employee newEmployee = new Employee();
        newEmployee.setFullName("Андрей Петров");
        newEmployee.setEmployeeExternalId(253L);

        Employee saved = employeeRepository.save(newEmployee);

        assertThat(saved.getEmployeeId()).isNotNull();
        assertThat(saved.getEmployeeExternalId()).isEqualTo(253L);
        assertThat(saved.getFullName()).isEqualTo("Андрей Петров");
    }
}

