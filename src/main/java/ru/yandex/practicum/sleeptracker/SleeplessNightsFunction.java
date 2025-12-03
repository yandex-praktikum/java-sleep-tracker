package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0);
        }

        // Находим даты начала и конца логирования
        LocalDate firstDate = sessions.stream()
                .map(s -> s.getSleepStart().toLocalDate())
                .min(LocalDate::compareTo)
                .orElseThrow();

        LocalDate lastDate = sessions.stream()
                .map(s -> s.getSleepEnd().toLocalDate())
                .max(LocalDate::compareTo)
                .orElseThrow();

        LocalTime firstStartTime = sessions.get(0).getSleepStart().toLocalTime();
        LocalDate firstNight = firstStartTime.isAfter(NOON)
                ? firstDate
                : firstDate.minusDays(1);

        int totalNights = Period.between(firstNight, lastDate).getDays();

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(this::isNightSleep)
                .flatMap(session -> getNightsForSession(session).stream())
                .collect(Collectors.toSet());

        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }

    private boolean isNightSleep(SleepingSession session) {
        LocalDate startDate = session.getSleepStart().toLocalDate();
        LocalDate endDate = session.getSleepEnd().toLocalDate();
        LocalTime startTime = session.getSleepStart().toLocalTime();
        LocalTime endTime = session.getSleepEnd().toLocalTime();

        if (!startDate.equals(endDate)) {
            return true;
        }

        if (startTime.isAfter(NOON) && (endTime.equals(NIGHT_START) || endTime.isBefore(NIGHT_END))) {
            return true;
        }

        return false;
    }

    private Set<LocalDate> getNightsForSession(SleepingSession session) {
        LocalDate startDate = session.getSleepStart().toLocalDate();
        LocalDate endDate = session.getSleepEnd().toLocalDate();
        LocalTime startTime = session.getSleepStart().toLocalTime();

        Set<LocalDate> nights = new java.util.HashSet<>();

        if (startTime.isAfter(NOON)) {
            nights.add(startDate);
        } else {
            nights.add(startDate.minusDays(1));
        }

        if (!startDate.equals(endDate)) {
            LocalDate currentDate = startDate.plusDays(1);
            while (!currentDate.isAfter(endDate.minusDays(1))) {
                nights.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }

        return nights;
    }
}
