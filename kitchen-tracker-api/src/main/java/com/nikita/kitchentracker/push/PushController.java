package com.nikita.kitchentracker.push;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
public class PushController {

    @Value("${vapid.public-key:}")
    private String vapidPublicKey;
    @Value("${notification.cron-token:}")
    private String cronToken;

    private final PushNotificationService pushNotificationService;
    private final NotificationScheduler notificationScheduler;

    public PushController(
            PushNotificationService pushNotificationService,
            NotificationScheduler notificationScheduler
    ) {
        this.pushNotificationService = pushNotificationService;
        this.notificationScheduler = notificationScheduler;
    }

    @GetMapping("/vapid-public-key")
    public String getVapidPublicKey() {
        if (vapidPublicKey == null || vapidPublicKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "VAPID public key is not configured."
            );
        }
        return vapidPublicKey;
    }

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@RequestBody PushSubscriptionDto dto) {
        pushNotificationService.saveSubscription(dto);
    }

    @DeleteMapping("/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@RequestBody Map<String, String> body) {
        String endpoint = body.get("endpoint");
        if (endpoint != null) {
            pushNotificationService.deleteSubscription(endpoint);
        }
    }

    @GetMapping("/run-expiry-alerts")
    public Map<String, String> runExpiryAlerts(
            @RequestParam String token,
            @RequestParam(defaultValue = "false") boolean force
    ) {
        if (cronToken == null || cronToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Notification cron token is not configured."
            );
        }
        if (!cronToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid notification cron token.");
        }
        return notificationScheduler.runExpiryAlertJobIfDue(force);
    }
}
