package com.wzu.exam.service.impl;

import com.wzu.exam.dto.SystemSettingsDTO;
import com.wzu.exam.model.SystemSettings;
import com.wzu.exam.repository.SystemSettingsRepository;
import com.wzu.exam.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingsServiceImpl implements SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;

    @Override
    public SystemSettingsDTO getSystemSettings() {
        log.info("获取系统设置");
        
        // 获取系统设置，如果不存在则创建默认设置
        SystemSettings settings = getOrCreateSettings();
        
        // 转换为DTO
        return convertToDTO(settings);
    }

    @Override
    @Transactional
    public SystemSettingsDTO saveSystemSettings(SystemSettingsDTO settingsDTO) {
        log.info("保存系统设置: {}", settingsDTO);
        
        // 获取系统设置，如果不存在则创建默认设置
        SystemSettings settings = getOrCreateSettings();
        
        // 更新设置
        settings.setSystemName(settingsDTO.getSystemName());
        settings.setSystemDescription(settingsDTO.getSystemDescription());
        settings.setAdminEmail(settingsDTO.getAdminEmail());
        settings.setDefaultExamDuration(settingsDTO.getDefaultExamDuration());
        settings.setDefaultPassScore(settingsDTO.getDefaultPassScore());
        
        // 保存设置
        settings = systemSettingsRepository.save(settings);
        log.info("系统设置保存成功: {}", settings);
        
        // 转换为DTO
        return convertToDTO(settings);
    }
    
    /**
     * 获取系统设置，如果不存在则创建默认设置
     */
    private SystemSettings getOrCreateSettings() {
        List<SystemSettings> settingsList = systemSettingsRepository.findAll();
        
        if (settingsList.isEmpty()) {
            // 创建默认设置
            SystemSettings defaultSettings = new SystemSettings();
            defaultSettings.setSystemName("在线考试系统");
            defaultSettings.setSystemDescription("为学生提供便捷的在线考试体验");
            defaultSettings.setAdminEmail("admin@example.com");
            defaultSettings.setDefaultExamDuration(60);
            defaultSettings.setDefaultPassScore(60);
            
            return systemSettingsRepository.save(defaultSettings);
        }
        
        // 返回第一个设置
        return settingsList.get(0);
    }
    
    /**
     * 将实体转换为DTO
     */
    private SystemSettingsDTO convertToDTO(SystemSettings settings) {
        SystemSettingsDTO dto = new SystemSettingsDTO();
        dto.setSystemName(settings.getSystemName());
        dto.setSystemDescription(settings.getSystemDescription());
        dto.setAdminEmail(settings.getAdminEmail());
        dto.setDefaultExamDuration(settings.getDefaultExamDuration());
        dto.setDefaultPassScore(settings.getDefaultPassScore());
        return dto;
    }
}
