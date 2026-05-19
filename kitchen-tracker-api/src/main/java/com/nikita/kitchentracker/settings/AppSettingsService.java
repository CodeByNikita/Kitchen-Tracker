package com.nikita.kitchentracker.settings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.model.AppSettings;
import com.nikita.kitchentracker.repository.AppSettingsRepository;

@Service
public class AppSettingsService {
    private static final Long SETTINGS_ID = 1L;

    private final AppSettingsRepository repository;

    public AppSettingsService(AppSettingsRepository repository) {
        this.repository = repository;
    }

    public AppSettings getSettings() {
        AppSettings settings = repository.findById(SETTINGS_ID).orElseGet(() -> repository.save(new AppSettings()));
        if (settings.getNotificationTimes() == null || settings.getNotificationTimes().isEmpty()) {
            settings.setNotificationTimes(new ArrayList<>(List.of(LocalTime.of(9, 0))));
            return repository.save(settings);
        }
        return settings;
    }

    public AppSettings updateNotificationTime(LocalTime notificationTime) {
        return updateNotificationTimes(notificationTime == null ? List.of() : List.of(notificationTime));
    }

    public AppSettings updateNotificationTimes(List<LocalTime> notificationTimes) {
        AppSettings settings = getSettings();
        List<LocalTime> source = notificationTimes == null ? List.of() : notificationTimes;
        List<LocalTime> cleaned = source.stream()
                .filter(time -> time != null)
                .distinct()
                .sorted()
                .toList();
        settings.setNotificationTimes(new ArrayList<>(cleaned.isEmpty() ? List.of(LocalTime.of(9, 0)) : cleaned));
        return repository.save(settings);
    }

    public Optional<LocalTime> nextDueNotificationTime(LocalDate today, LocalTime now) {
        AppSettings settings = getSettings();
        if (!today.equals(settings.getLastNotificationDate())) {
            settings.setLastNotificationDate(today);
            settings.setSentNotificationTimes(new ArrayList<>());
            settings = repository.save(settings);
        }

        List<LocalTime> sent = settings.getSentNotificationTimes() == null
                ? List.of()
                : settings.getSentNotificationTimes();

        return settings.getNotificationTimes().stream()
                .filter(time -> !now.isBefore(time))
                .filter(time -> !sent.contains(time))
                .min(Comparator.naturalOrder());
    }

    public void markNotificationSent(LocalDate today, LocalTime notificationTime) {
        AppSettings settings = getSettings();
        settings.setLastNotificationDate(today);
        List<LocalTime> sent = settings.getSentNotificationTimes() == null
                ? new ArrayList<>()
                : new ArrayList<>(settings.getSentNotificationTimes());
        if (!sent.contains(notificationTime)) {
            sent.add(notificationTime);
        }
        settings.setSentNotificationTimes(sent);
        repository.save(settings);
    }
}
