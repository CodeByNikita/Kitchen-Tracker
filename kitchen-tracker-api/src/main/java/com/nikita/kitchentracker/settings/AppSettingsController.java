package com.nikita.kitchentracker.settings;

import com.nikita.kitchentracker.model.AppSettings;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class AppSettingsController {
    private final AppSettingsService service;

    public AppSettingsController(AppSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public AppSettings getSettings() {
        return service.getSettings();
    }

    @PutMapping
    public AppSettings updateSettings(@Valid @RequestBody AppSettingsDto dto) {
        if (dto.getNotificationTimes() != null && !dto.getNotificationTimes().isEmpty()) {
            return service.updateNotificationTimes(dto.getNotificationTimes());
        }
        return service.updateNotificationTime(dto.getNotificationTime());
    }
}
