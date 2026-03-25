package com.brahmavanam.japa.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JapaDashboardDTO {

    // Summary
    private int todayMalas;
    private Integer todayTarget;
    private int currentStreak;
    private int longestStreak;
    private int weeklyMalas;
    private int monthlyMalas;
    private int allTimeMalas;
    private double avgIntensityThisWeek; // 1=DISTRACTED, 2=MODERATE, 3=FOCUSED, 4=DEEP

    // Charts
    private List<DailyCount> barChart;       // daily malas for last N days
    private List<DailyIntensity> lineChart;  // daily avg intensity for last N days
    private Map<String, Integer> heatmap;    // date string -> total malas

    @Data
    public static class DailyCount {
        private String date;
        private int malas;

        public DailyCount(String date, int malas) {
            this.date = date;
            this.malas = malas;
        }
    }

    @Data
    public static class DailyIntensity {
        private String date;
        private double avgIntensity;

        public DailyIntensity(String date, double avgIntensity) {
            this.date = date;
            this.avgIntensity = avgIntensity;
        }
    }
}
