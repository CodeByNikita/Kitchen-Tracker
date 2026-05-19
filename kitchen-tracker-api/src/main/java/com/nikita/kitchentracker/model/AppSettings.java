package com.nikita.kitchentracker.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_settings")
public class AppSettings {
    @Id
    private Long id = 1L;
    @ElementCollection
    private List<LocalTime> notificationTimes = new ArrayList<>(List.of(LocalTime.of(9, 0)));
    private LocalDate lastNotificationDate;
    @ElementCollection
    private List<LocalTime> sentNotificationTimes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LocalTime> getNotificationTimes() {
        return notificationTimes;
    }

    public void setNotificationTimes(List<LocalTime> notificationTimes) {
        this.notificationTimes = notificationTimes;
    }

    public LocalDate getLastNotificationDate() {
        return lastNotificationDate;
    }

    public void setLastNotificationDate(LocalDate lastNotificationDate) {
        this.lastNotificationDate = lastNotificationDate;
    }

    public List<LocalTime> getSentNotificationTimes() {
        return sentNotificationTimes;
    }

    public void setSentNotificationTimes(List<LocalTime> sentNotificationTimes) {
        this.sentNotificationTimes = sentNotificationTimes;
    }
}
