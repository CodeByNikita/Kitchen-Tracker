package com.nikita.kitchentracker.repository;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    Optional<PushSubscription> findByEndpoint(String endpoint);

    Optional<PushSubscription> findByEndpointAndOwner(String endpoint, AppUser owner);

    List<PushSubscription> findAllByOwner(AppUser owner);
}
