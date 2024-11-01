package com.TASKTWOO.TASKTOO.dao;
import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class ProfilePictureDaoImpl implements ProfilePictureDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProfilePictureDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveProfilePicture(ProfilePicture profilePicture) {
        String sql = "INSERT INTO profile_picture (user_id, file_path, file_name, file_type, created_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                profilePicture.getUserId(),
                profilePicture.getFilePath(),
                profilePicture.getFileName(),
                profilePicture.getFileType(),
                new Timestamp(System.currentTimeMillis()) // Set created_at
        );
    }

    @Override
    public ProfilePicture getProfilePictureByUserId(Long userId) {
        String sql = "SELECT * FROM profile_picture WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, profilePictureRowMapper);
        } catch (EmptyResultDataAccessException e) {
            // Handle case where no profile picture is found
            System.out.println("No profile picture found for user_id: " + userId); // Logging
            return null;  // Or you can throw a custom exception
        }
    }

    private final RowMapper<ProfilePicture> profilePictureRowMapper = (rs, rowNum) -> {
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUserId(rs.getLong("user_id"));
        profilePicture.setFilePath(rs.getString("file_path"));
        profilePicture.setFileName(rs.getString("file_name"));
        profilePicture.setFileType(rs.getString("file_type"));
        profilePicture.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // Map created_at
        return profilePicture;
    };
}
