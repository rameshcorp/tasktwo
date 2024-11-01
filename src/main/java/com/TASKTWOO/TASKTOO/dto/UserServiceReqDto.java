package com.TASKTWOO.TASKTOO.dto;

import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import lombok.Data;

@Data
public class UserServiceReqDto {
    private String userId;       // ID of the user
    private String filePath;     // File path of the profile picture
    private String fileName;     // Name of the profile picture file
    private String fileType;     // Type of the profile picture file

    // Converts UserServiceReqDto to a ProfilePicture object
    public ProfilePicture toProfilePicture() {
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUserId(Long.parseLong(this.userId));
        profilePicture.setFilePath(this.filePath);
        profilePicture.setFileName(this.fileName);
        profilePicture.setFileType(this.fileType);
        // Set createdAt to the current time when creating the ProfilePicture object
        profilePicture.setCreatedAt(java.time.LocalDateTime.now());
        return profilePicture;
    }
}
