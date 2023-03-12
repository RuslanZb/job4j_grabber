package ru.job4j.quartz.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    public void whenDateTimeWithZoneMustBeDateTimeWithoutZone() {
        String date = "2023-03-11T12:27:34+03:00";
        LocalDateTime rsl = new HabrCareerDateTimeParser().parse(date);
        LocalDateTime expected = LocalDateTime.parse("2023-03-11T12:27:34");
        assertThat(expected).isEqualTo(rsl);
    }
}