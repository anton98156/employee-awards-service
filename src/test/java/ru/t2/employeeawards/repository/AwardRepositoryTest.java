package ru.t2.employeeawards.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.t2.employeeawards.model.Award;
import ru.t2.employeeawards.model.Employee;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AwardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AwardRepository awardRepository;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setFullName("Дмитрий Кузнецов");
        employee.setEmployeeExternalId(189L);
        entityManager.persistAndFlush(employee);

        Award award = new Award();
        award.setAwardExternalId(503L);
        award.setAwardName("Самый эффективный руководитель");
        award.setReceivedDate(LocalDate.of(2025, 3, 15));
        award.setEmployee(employee);
        entityManager.persistAndFlush(award);
    }

    @Test
    void findByAwardExternalId_WhenAwardExists_ShouldReturnAward() {
        Optional<Award> found = awardRepository.findByAwardExternalId(503L);

        assertThat(found).isPresent();
        assertThat(found.get().getAwardExternalId()).isEqualTo(503L);
        assertThat(found.get().getAwardName()).isEqualTo("Самый эффективный руководитель");
        assertThat(found.get().getReceivedDate()).isEqualTo(LocalDate.of(2025, 3, 15));
        assertThat(found.get().getEmployee()).isNotNull();
    }

    @Test
    void findByAwardExternalId_WhenAwardNotExists_ShouldReturnEmpty() {
        Optional<Award> found = awardRepository.findByAwardExternalId(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistAward() {
        Award adaward = new Award();
        Employee employee = new Employee();
        adaward.setAwardExternalId(600L);
        adaward.setAwardName("Продавец месяца");
        adaward.setReceivedDate(LocalDate.of(2025, 6, 1));
        adaward.setEmployee(employee);

        Award savedAward = awardRepository.save(adaward);
        assertThat(savedAward.getAwardId()).isNotNull();
        assertThat(savedAward.getAwardExternalId()).isEqualTo(600L);
        assertThat(savedAward.getAwardName()).isEqualTo("Продавец месяца");
        assertThat(savedAward.getReceivedDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(savedAward.getEmployee()).isEqualTo(employee);
    }
}

