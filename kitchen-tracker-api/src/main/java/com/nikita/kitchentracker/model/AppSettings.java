package com.nikita.kitchentracker.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "app_settings")
public class AppSettings {
    @Id
    private Long id = 1L;
    @Column(name = "notification_times")
    private String notificationTimesValue = "09:00";
    private LocalDate lastNotificationDate;
    @Column(name = "sent_notification_times")
    private String sentNotificationTimesValue = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public List<LocalTime> getNotificationTimes() {
        return parseTimes(notificationTimesValue);
    }

    public void setNotificationTimes(List<LocalTime> notificationTimes) {
        this.notificationTimesValue = formatTimes(notificationTimes);
    }

    public LocalDate getLastNotificationDate() {
        return lastNotificationDate;
    }

    public void setLastNotificationDate(LocalDate lastNotificationDate) {
        this.lastNotificationDate = lastNotificationDate;
    }

    @Transient
    public List<LocalTime> getSentNotificationTimes() {
        return parseTimes(sentNotificationTimesValue);
    }

    public void setSentNotificationTimes(List<LocalTime> sentNotificationTimes) {
        this.sentNotificationTimesValue = formatTimes(sentNotificationTimes);
    }

    private static List<LocalTime> parseTimes(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        List<LocalTime> times = new ArrayList<>();
        for (String part : value.split(",")) {
            if (!part.isBlank()) {
                times.add(LocalTime.parse(part.trim()));
            }
        }
        return times;
    }

    private static String formatTimes(List<LocalTime> times) {
        List<LocalTime> source = times == null ? List.of() : times;
        return source.stream()
                .map(LocalTime::toString)
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }
}
