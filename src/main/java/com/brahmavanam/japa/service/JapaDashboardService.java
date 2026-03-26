package com.brahmavanam.japa.service;

import com.brahmavanam.japa.dto.JapaDashboardDTO;
import com.brahmavanam.japa.dto.JapaDashboardDTO.DailyCount;
import com.brahmavanam.japa.dto.JapaDashboardDTO.DailyIntensity;
import com.brahmavanam.japa.model.JapaLog;
import com.brahmavanam.japa.model.JapaLog.Intensity;
import com.brahmavanam.japa.repository.JapaLogRepository;
import com.brahmavanam.japa.repository.JapaTargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JapaDashboardService {

    @Autowired
    private JapaLogRepository japaLogRepository;

    @Autowired
    private JapaTargetRepository japaTargetRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public JapaDashboardDTO getDashboard(int userId, int barChartDays) {
        List<JapaLog> allLogs = japaLogRepository.findByUserIdOrderByLoggedAtDesc(userId);

        LocalDate today = LocalDate.now();
        JapaDashboardDTO dto = new JapaDashboardDTO();

        // Today's malas
        dto.setTodayMalas(sumMalas(allLogs, today, today));

        // Today's target
        japaTargetRepository
                .findTopByUserIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(userId, today)
                .ifPresent(t -> dto.setTodayTarget(t.getTargetMalas()));

        // Streaks
        int[] streaks = calculateStreaks(allLogs, today);
        dto.setCurrentStreak(streaks[0]);
        dto.setLongestStreak(streaks[1]);

        // Totals
        dto.setWeeklyMalas(sumMalas(allLogs, today.minusDays(6), today));
        dto.setMonthlyMalas(sumMalas(allLogs, today.withDayOfMonth(1), today));
        dto.setAllTimeMalas(allLogs.stream().mapToInt(JapaLog::getMalas).sum());

        // Avg intensity this week
        dto.setAvgIntensityThisWeek(avgIntensity(allLogs, today.minusDays(6), today));

        // Bar chart
        dto.setBarChart(buildBarChart(allLogs, today, barChartDays));

        // Line chart (last 30 days)
        dto.setLineChart(buildLineChart(allLogs, today, 30));

        // Heatmap (last 6 months)
        dto.setHeatmap(buildHeatmap(allLogs, today.minusMonths(6), today));

        return dto;
    }

    private int sumMalas(List<JapaLog> logs, LocalDate from, LocalDate to) {
        return logs.stream()
                .filter(l -> !l.getLoggedAt().toLocalDate().isBefore(from)
                        && !l.getLoggedAt().toLocalDate().isAfter(to))
                .mapToInt(JapaLog::getMalas).sum();
    }

    private double avgIntensity(List<JapaLog> logs, LocalDate from, LocalDate to) {
        return logs.stream()
                .filter(l -> !l.getLoggedAt().toLocalDate().isBefore(from)
                        && !l.getLoggedAt().toLocalDate().isAfter(to))
                .mapToInt(l -> intensityScore(l.getIntensity()))
                .average().orElse(0);
    }

    private int intensityScore(Intensity intensity) {
        return switch (intensity) {
            case DISTRACTED -> 1;
            case MODERATE -> 2;
            case FOCUSED -> 3;
            case DEEP -> 4;
        };
    }

    private int[] calculateStreaks(List<JapaLog> logs, LocalDate today) {
        Set<LocalDate> logDates = logs.stream()
                .map(l -> l.getLoggedAt().toLocalDate())
                .collect(Collectors.toSet());

        int current = 0;
        LocalDate cursor = today;
        while (logDates.contains(cursor)) {
            current++;
            cursor = cursor.minusDays(1);
        }

        int longest = 0, running = 0;
        List<LocalDate> sorted = logDates.stream().sorted().collect(Collectors.toList());
        for (int i = 0; i < sorted.size(); i++) {
            if (i == 0 || sorted.get(i).equals(sorted.get(i - 1).plusDays(1))) {
                running++;
            } else {
                running = 1;
            }
            longest = Math.max(longest, running);
        }

        return new int[]{current, longest};
    }

    private List<DailyCount> buildBarChart(List<JapaLog> logs, LocalDate today, int days) {
        Map<LocalDate, Integer> byDate = logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getLoggedAt().toLocalDate(),
                        Collectors.summingInt(JapaLog::getMalas)));

        List<DailyCount> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            result.add(new DailyCount(date.format(DATE_FMT), byDate.getOrDefault(date, 0)));
        }
        return result;
    }

    private List<DailyIntensity> buildLineChart(List<JapaLog> logs, LocalDate today, int days) {
        Map<LocalDate, Double> byDate = logs.stream()
                .filter(l -> !l.getLoggedAt().toLocalDate().isBefore(today.minusDays(days - 1)))
                .collect(Collectors.groupingBy(
                        l -> l.getLoggedAt().toLocalDate(),
                        Collectors.averagingInt(l -> intensityScore(l.getIntensity()))));

        List<DailyIntensity> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            result.add(new DailyIntensity(date.format(DATE_FMT), byDate.getOrDefault(date, 0.0)));
        }
        return result;
    }

    private Map<String, Integer> buildHeatmap(List<JapaLog> logs, LocalDate from, LocalDate to) {
        return logs.stream()
                .filter(l -> !l.getLoggedAt().toLocalDate().isBefore(from)
                        && !l.getLoggedAt().toLocalDate().isAfter(to))
                .collect(Collectors.groupingBy(
                        l -> l.getLoggedAt().toLocalDate().format(DATE_FMT),
                        Collectors.summingInt(JapaLog::getMalas)));
    }
}
