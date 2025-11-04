package ru.t2.employeeawards.utils;

import ru.t2.employeeawards.dto.AwardFileRecord;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Утилитарный класс для проверок AwardFileRecord в тестах
 */
public final class AwardFileRecordAssertions {
    private AwardFileRecordAssertions() {
        // Утилитарный класс
    }

    /**
     * Проверяет, что фактическая запись соответствует ожидаемой.
     *
     * @param actual фактическая запись
     * @param expected ожидаемая запись
     */
    public static void assertRecord(AwardFileRecord actual, AwardFileRecord expected) {
        assertThat(actual.getEmployeeExternalId()).isEqualTo(expected.getEmployeeExternalId());
        assertThat(actual.getEmployeeFullName()).isEqualTo(expected.getEmployeeFullName());
        assertThat(actual.getAwardExternalId()).isEqualTo(expected.getAwardExternalId());
        assertThat(actual.getAwardName()).isEqualTo(expected.getAwardName());
        assertThat(actual.getReceivedDate()).isEqualTo(expected.getReceivedDate());
    }
}
