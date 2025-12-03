package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronoTypeFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime OWL_SLEEP_TIME = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_TIME = LocalTime.of(9, 0);
    private static final LocalTime LARK_SLEEP_TIME = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_TIME = LocalTime.of(7, 0);
    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        List<SleepingSession> nightSessions = sessions.stream()
                .filter(this::isNightSleep)
                .collect(Collectors.toList());

        if (nightSessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", ChronoType.DOVE);
        }

        Map<ChronoType, Long> chronoTypeCounts = nightSessions.stream()
                .map(this::classifySession)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maxCount = chronoTypeCounts.values().stream()
                .max(Long::compare)
                .orElse(0L);

        long typesWithMaxCount = chronoTypeCounts.values().stream()
                .filter(count -> count == maxCount)
                .count();

        if (typesWithMaxCount > 1) {
            return new SleepAnalysisResult("Хронотип пользователя", ChronoType.DOVE);
        }

        ChronoType dominantType = chronoTypeCounts.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(ChronoType.DOVE);

        return new SleepAnalysisResult("Хронотип пользователя", dominantType);
    }

    // Классифицирует сессию по хронотипу
    private ChronoType classifySession(SleepingSession session) {
        LocalTime sleepTime = session.getSleepStart().toLocalTime();
        LocalTime wakeTime = session.getSleepEnd().toLocalTime();

        boolean isOwl = (sleepTime.isAfter(OWL_SLEEP_TIME) || sleepTime.equals(OWL_SLEEP_TIME)) &&
                (wakeTime.isAfter(OWL_WAKE_TIME) || wakeTime.equals(OWL_WAKE_TIME));

        boolean isLark = sleepTime.isBefore(LARK_SLEEP_TIME) &&
                wakeTime.isBefore(LARK_WAKE_TIME);

        if (isOwl) {
            return ChronoType.OWL;
        } else if (isLark) {
            return ChronoType.LARK;
        } else {
            return ChronoType.DOVE;
        }
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
}
