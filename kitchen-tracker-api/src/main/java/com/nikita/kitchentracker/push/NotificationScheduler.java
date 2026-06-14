package com.nikita.kitchentracker.push;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.auth.AppUserRepository;
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
    private final AppUserRepository userRepository;

    public NotificationScheduler(
            KitchenService kitchenService,
            PushNotificationService pushService,
            AppSettingsService settingsService,
            AppUserRepository userRepository
    ) {
        this.kitchenService = kitchenService;
        this.pushService = pushService;
        this.settingsService = settingsService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void sendDailyExpiryAlerts() {
        runExpiryAlertJobIfDue(false);
    }

    public Map<String, String> runExpiryAlertJobIfDue(boolean force) {
        LocalDate today = LocalDate.now();
        int sent = 0;
        int expiringCount = 0;
        for (AppUser user : userRepository.findAll()) {
            UserNotificationResult result = runForUser(user, today, force);
            if (result.sent()) {
                sent++;
                expiringCount += result.count();
            }
        }
        if (sent == 0) {
            return Map.of("status", "not due");
        }
        return Map.of("status", "sent", "users", String.valueOf(sent), "count", String.valueOf(expiringCount));
    }

    private UserNotificationResult runForUser(AppUser user, LocalDate today, boolean force) {
        Optional<LocalTime> dueTime = force
                ? Optional.of(LocalTime.now().withSecond(0).withNano(0))
                : settingsService.nextDueNotificationTime(user, today, LocalTime.now());
        if (dueTime.isEmpty()) {
            return new UserNotificationResult(false, 0);
        }

        log.info("Running expiry notification job for {}", user.getEmail());
        List<Item> expiring = kitchenService.getExpiringSoonInclusive(user, 3);
        if (expiring.isEmpty()) {
            settingsService.markNotificationSent(user, today, dueTime.get());
            return new UserNotificationResult(true, 0);
        }

        List<Item> expired = expiring.stream().filter(i -> i.getExpiryDate().isBefore(today)).toList();
        List<Item> expiresTODAY = expiring.stream().filter(i -> i.getExpiryDate().isEqual(today)).toList();
        List<Item> soon = expiring.stream().filter(i -> i.getExpiryDate().isAfter(today)).toList();

        sendGroup(user, expired, "kt-expired",
                singular(expired, "%s has expired", "%d items have expired"),
                expired.size() == 1 ? "Check your kitchen and discard if needed." : names(expired));

        sendGroup(user, expiresTODAY, "kt-today",
                singular(expiresTODAY, "%s expires today", "%d items expire today"),
                expiresTODAY.size() == 1 ? "Use it today!" : names(expiresTODAY));

        sendGroup(user, soon, "kt-soon",
                singular(soon, "%s expires soon", "%d items expiring soon"),
                names(soon));

        settingsService.markNotificationSent(user, today, dueTime.get());
        return new UserNotificationResult(true, expiring.size());
    }

    private void sendGroup(AppUser user, List<Item> group, String tag, String title, String body) {
        if (group.isEmpty()) return;
        pushService.sendNotification(user, title, body, tag);
    }

    private String singular(List<Item> group, String oneTemplate, String manyTemplate) {
        if (group.size() == 1) return String.format(oneTemplate, group.get(0).getName());
        return String.format(manyTemplate, group.size());
    }

    private String names(List<Item> items) {
        return items.stream().map(Item::getName).collect(Collectors.joining(", "));
    }

    private record UserNotificationResult(boolean sent, int count) {
    }
}
