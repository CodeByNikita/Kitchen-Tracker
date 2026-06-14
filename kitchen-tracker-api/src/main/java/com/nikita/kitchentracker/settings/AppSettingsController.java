package com.nikita.kitchentracker.settings;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.auth.AuthService;
import com.nikita.kitchentracker.model.AppSettings;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class AppSettingsController {
    private final AppSettingsService service;
    private final AuthService authService;

    public AppSettingsController(AppSettingsService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping
    public AppSettings getSettings(@RequestHeader(name = "Authorization", required = false) String authorization) {
        return service.getSettings(user(authorization));
    }

    @PutMapping
    public AppSettings updateSettings(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody AppSettingsDto dto
    ) {
        AppUser user = user(authorization);
        if (dto.getNotificationTimes() != null && !dto.getNotificationTimes().isEmpty()) {
            return service.updateNotificationTimes(user, dto.getNotificationTimes());
        }
        return service.updateNotificationTime(user, dto.getNotificationTime());
    }

    private AppUser user(String authorization) {
        return authService.requireUser(authorization);
    }
}
