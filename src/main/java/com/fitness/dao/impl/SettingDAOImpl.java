package com.fitness.dao.impl;

import com.fitness.dao.SettingDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.AppSetting;
import com.fitness.util.ConnectionProvider;
import com.fitness.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SettingDAO with prepared statements.
 */
public class SettingDAOImpl implements SettingDAO {

    private static final Logger logger = LoggerFactory.getLogger(SettingDAOImpl.class);
    private final ConnectionProvider connectionProvider;

    public SettingDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public SettingDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<AppSetting> findAll() {
        List<AppSetting> list = new ArrayList<>();
        String sql = "SELECT * FROM app_settings ORDER BY setting_key ASC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AppSetting s = new AppSetting();
                s.setKey(rs.getString("setting_key"));
                s.setValue(rs.getString("setting_value"));
                s.setDescription(rs.getString("description"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(s);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving settings", e);
            throw new DatabaseException("Failed to fetch settings: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Optional<String> getSettingValue(String key) {
        String sql = "SELECT setting_value FROM app_settings WHERE setting_key = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("setting_value"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving setting key: {}", key, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateSetting(String key, String value) {
        String sql = "UPDATE app_settings SET setting_value = ? WHERE setting_key = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, key);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating setting: {}", key, e);
            throw new DatabaseException("Failed to update setting: " + e.getMessage(), e);
        }
    }
}
