package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.Duration;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationInMinutes() {
        return Duration.between(sleepStart, sleepEnd).toMinutes();
    }

    @Override
    public String toString() {
        return String.format("Sleep: %s -> %s (%s)", sleepStart, sleepEnd, quality);
    }
}
