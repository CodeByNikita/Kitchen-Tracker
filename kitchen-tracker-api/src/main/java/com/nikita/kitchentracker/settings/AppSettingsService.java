package com.nikita.kitchentracker.settings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.AppSettings;
import com.nikita.kitchentracker.repository.AppSettingsRepository;

@Service
public class AppSettingsService {
    private final AppSettingsRepository repository;

    public AppSettingsService(AppSettingsRepository repository) {
        this.repository = repository;
    }

    public AppSettings getSettings(AppUser user) {
        AppSettings settings = repository.findByOwner(user).orElseGet(() -> {
            AppSettings created = new AppSettings();
            created.setOwner(user);
            return repository.save(created);
        });
        if (settings.getNotificationTimes() == null || settings.getNotificationTimes().isEmpty()) {
            settings.setNotificationTimes(new ArrayList<>(List.of(LocalTime.of(9, 0))));
            return repository.save(settings);
        }
        return settings;
    }

    public AppSettings updateNotificationTime(AppUser user, LocalTime notificationTime) {
        return updateNotificationTimes(user, notificationTime == null ? List.of() : List.of(notificationTime));
    }

    public AppSettings updateNotificationTimes(AppUser user, List<LocalTime> notificationTimes) {
        AppSettings settings = getSettings(user);
        List<LocalTime> source = notificationTimes == null ? List.of() : notificationTimes;
        List<LocalTime> cleaned = source.stream()
                .filter(time -> time != null)
                .distinct()
                .sorted()
                .toList();
        settings.setNotificationTimes(new ArrayList<>(cleaned.isEmpty() ? List.of(LocalTime.of(9, 0)) : cleaned));
        return repository.save(settings);
    }

    public Optional<LocalTime> nextDueNotificationTime(AppUser user, LocalDate today, LocalTime now) {
        AppSettings settings = getSettings(user);
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

    public void markNotificationSent(AppUser user, LocalDate today, LocalTime notificationTime) {
        AppSettings settings = getSettings(user);
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
