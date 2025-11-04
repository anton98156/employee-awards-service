package ru.t2.employeeawards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t2.employeeawards.model.Award;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {
    /**
     * Поиск награды по внешнему идентификатору
     * @param awardExternalId внешний идентификатор награды
     * @return награда
     */
    Optional<Award> findByAwardExternalId(Long awardExternalId);
}
