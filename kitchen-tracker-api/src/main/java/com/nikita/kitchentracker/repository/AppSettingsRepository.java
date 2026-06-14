package com.nikita.kitchentracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.AppSettings;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
    Optional<AppSettings> findByOwner(AppUser owner);
}
