package com.fitness.dao;

import com.fitness.model.AppSetting;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for system configuration settings.
 */
public interface SettingDAO {

    List<AppSetting> findAll();

    Optional<String> getSettingValue(String key);

    boolean updateSetting(String key, String value);
}
