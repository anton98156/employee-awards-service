package ru.t2.employeeawards.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t2.employeeawards.Model.Award;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {
    Optional<Award> findByAwardExternalId(Long awardExternalId);
}
