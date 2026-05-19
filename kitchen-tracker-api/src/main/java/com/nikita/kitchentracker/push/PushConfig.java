package com.nikita.kitchentracker.push;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.security.Security;

@Configuration
public class PushConfig {

    @Bean
    @ConditionalOnProperty(name = { "vapid.public-key", "vapid.private-key" })
    PushService pushService(
            @Value("${vapid.public-key}") String publicKey,
            @Value("${vapid.private-key}") String privateKey
    ) throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        return new PushService(publicKey, privateKey, "mailto:admin@kitchen-tracker.app");
    }
}
