package com.nikita.kitchentracker.push;

import com.nikita.kitchentracker.model.PushSubscription;
import com.nikita.kitchentracker.repository.PushSubscriptionRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    private final PushSubscriptionRepository subscriptionRepo;
    private final ObjectProvider<PushService> pushServiceProvider;

    public PushNotificationService(
            PushSubscriptionRepository subscriptionRepo,
            ObjectProvider<PushService> pushServiceProvider
    ) {
        this.subscriptionRepo = subscriptionRepo;
        this.pushServiceProvider = pushServiceProvider;
    }

    public void saveSubscription(PushSubscriptionDto dto) {
        Optional<PushSubscription> existing = subscriptionRepo.findByEndpoint(dto.getEndpoint());
        PushSubscription sub = existing.orElse(new PushSubscription());
        sub.setEndpoint(dto.getEndpoint());
        sub.setP256dh(dto.getKeys().getP256dh());
        sub.setAuth(dto.getKeys().getAuth());
        subscriptionRepo.save(sub);
    }

    public void deleteSubscription(String endpoint) {
        subscriptionRepo.findByEndpoint(endpoint).ifPresent(subscriptionRepo::delete);
    }

    public void broadcastNotification(String title, String body, String tag) {
        PushService pushService = pushServiceProvider.getIfAvailable();
        if (pushService == null) {
            log.info("Skipping push broadcast because VAPID keys are not configured.");
            return;
        }
        List<PushSubscription> subs = subscriptionRepo.findAll();
        if (subs.isEmpty()) return;
        String payload = buildPayload(title, body, tag);
        for (PushSubscription sub : subs) {
            sendToOne(pushService, sub, payload);
        }
    }

    private void sendToOne(PushService pushService, PushSubscription sub, String payload) {
        try {
            ECPublicKey p256dhKey = (ECPublicKey) Utils.loadPublicKey(sub.getP256dh());
            byte[] authBytes = Base64.getUrlDecoder().decode(sub.getAuth());
            Notification notification = new Notification(
                    sub.getEndpoint(),
                    p256dhKey,
                    authBytes,
                    payload.getBytes(StandardCharsets.UTF_8)
            );
            org.apache.http.HttpResponse response = pushService.send(notification);
            int status = response.getStatusLine().getStatusCode();
            if (status == 410) {
                log.info("Subscription expired (410), removing: {}", sub.getEndpoint());
                subscriptionRepo.delete(sub);
            }
        } catch (Exception e) {
            log.error("Failed to send push to {}: {}", sub.getEndpoint(), e.getMessage());
        }
    }

    private String buildPayload(String title, String body, String tag) {
        return "{\"title\":\"" + esc(title) + "\",\"body\":\"" + esc(body) + "\",\"tag\":\"" + esc(tag) + "\"}";
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
