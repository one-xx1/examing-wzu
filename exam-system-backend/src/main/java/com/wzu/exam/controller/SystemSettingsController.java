package com.wzu.exam.controller;

import com.wzu.exam.dto.SystemSettingsDTO;
import com.wzu.exam.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    /**
     * 获取系统设置
     */
    @GetMapping
    public ResponseEntity<SystemSettingsDTO> getSystemSettings() {
        log.info("接收到获取系统设置请求");
        SystemSettingsDTO settings = systemSettingsService.getSystemSettings();
        log.info("返回系统设置: {}", settings);
        return ResponseEntity.ok(settings);
    }

    /**
     * 保存系统设置
     */
    @PostMapping
    public ResponseEntity<SystemSettingsDTO> saveSystemSettings(@RequestBody SystemSettingsDTO settingsDTO) {
        log.info("接收到保存系统设置请求: {}", settingsDTO);
        SystemSettingsDTO savedSettings = systemSettingsService.saveSystemSettings(settingsDTO);
        log.info("系统设置保存成功: {}", savedSettings);
        return ResponseEntity.ok(savedSettings);
    }
}
