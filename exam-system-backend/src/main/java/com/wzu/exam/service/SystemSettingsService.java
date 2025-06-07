package com.wzu.exam.service;

import com.wzu.exam.dto.SystemSettingsDTO;

public interface SystemSettingsService {
    /**
     * 获取系统设置
     * @return 系统设置DTO
     */
    SystemSettingsDTO getSystemSettings();
    
    /**
     * 保存系统设置
     * @param settingsDTO 系统设置DTO
     * @return 保存后的系统设置DTO
     */
    SystemSettingsDTO saveSystemSettings(SystemSettingsDTO settingsDTO);
}
