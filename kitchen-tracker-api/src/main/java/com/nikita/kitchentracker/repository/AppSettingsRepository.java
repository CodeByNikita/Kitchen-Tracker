package com.nikita.kitchentracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.model.AppSettings;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
