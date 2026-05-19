package com.nikita.kitchentracker.push;

import com.nikita.kitchentracker.model.Item;
import com.nikita.kitchentracker.settings.AppSettingsService;
import com.nikita.kitchentracker.service.KitchenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    private final KitchenService kitchenService;
    private final PushNotificationService pushService;
    private final AppSettingsService settingsService;

    public NotificationScheduler(
            KitchenService kitchenService,
            PushNotificationService pushService,
            AppSettingsService settingsService
    ) {
        this.kitchenService = kitchenService;
        this.pushService = pushService;
        this.settingsService = settingsService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void sendDailyExpiryAlerts() {
        runExpiryAlertJobIfDue(false);
    }

    public Map<String, String> runExpiryAlertJobIfDue(boolean force) {
        LocalDate today = LocalDate.now();
        Optional<LocalTime> dueTime = force
                ? Optional.of(LocalTime.now().withSecond(0).withNano(0))
                : settingsService.nextDueNotificationTime(today, LocalTime.now());
        if (dueTime.isEmpty()) {
            return Map.of("status", "not due");
        }

        log.info("Running expiry notification job");
        List<Item> expiring = kitchenService.getExpiringSoonInclusive(3);
        if (expiring.isEmpty()) {
            settingsService.markNotificationSent(today, dueTime.get());
            return Map.of("status", "no expiring items", "notificationTime", dueTime.get().toString());
        }

        List<Item> expired = expiring.stream().filter(i -> i.getExpiryDate().isBefore(today)).toList();
        List<Item> expiresTODAY = expiring.stream().filter(i -> i.getExpiryDate().isEqual(today)).toList();
        List<Item> soon = expiring.stream().filter(i -> i.getExpiryDate().isAfter(today)).toList();

        sendGroup(expired, "kt-expired",
                singular(expired, "%s has expired", "%d items have expired"),
                expired.size() == 1 ? "Check your kitchen and discard if needed." : names(expired));

        sendGroup(expiresTODAY, "kt-today",
                singular(expiresTODAY, "%s expires today", "%d items expire today"),
                expiresTODAY.size() == 1 ? "Use it today!" : names(expiresTODAY));

        sendGroup(soon, "kt-soon",
                singular(soon, "%s expires soon", "%d items expiring soon"),
                names(soon));

        settingsService.markNotificationSent(today, dueTime.get());
        return Map.of(
                "status", "sent",
                "count", String.valueOf(expiring.size()),
                "notificationTime", dueTime.get().toString()
        );
    }

    private void sendGroup(List<Item> group, String tag, String title, String body) {
        if (group.isEmpty()) return;
        pushService.broadcastNotification(title, body, tag);
    }

    private String singular(List<Item> group, String oneTemplate, String manyTemplate) {
        if (group.size() == 1) return String.format(oneTemplate, group.get(0).getName());
        return String.format(manyTemplate, group.size());
    }

    private String names(List<Item> items) {
        return items.stream().map(Item::getName).collect(Collectors.joining(", "));
    }
}
