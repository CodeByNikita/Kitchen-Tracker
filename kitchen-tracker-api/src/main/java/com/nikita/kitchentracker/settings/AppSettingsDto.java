package com.nikita.kitchentracker.settings;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public class AppSettingsDto {
    private LocalTime notificationTime;
    private List<@NotNull LocalTime> notificationTimes = new ArrayList<>();

    public LocalTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public List<LocalTime> getNotificationTimes() {
        return notificationTimes;
    }

    public void setNotificationTimes(List<LocalTime> notificationTimes) {
        this.notificationTimes = notificationTimes;
    }
}
