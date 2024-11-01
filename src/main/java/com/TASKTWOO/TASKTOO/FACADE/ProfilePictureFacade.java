package com.TASKTWOO.TASKTOO.FACADE;

import com.TASKTWOO.TASKTOO.dao.ProfilePictureDao;
import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import com.TASKTWOO.TASKTOO.dto.ResponseDto;
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ProfilePictureFacade {

    private static final Logger logger = LoggerFactory.getLogger(ProfilePictureFacade.class);
    private final String uploadDir = "C:/actual/upload/directory"; // Update with actual directory path
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // Max file size: 5 MB
    private final ProfilePictureDao profilePictureDao;

    public ProfilePictureFacade(ProfilePictureDao profilePictureDao) {
        this.profilePictureDao = profilePictureDao;

        // Ensure the upload directory exists
        try {
            if (!Files.exists(Paths.get(uploadDir))) {
                Files.createDirectories(Paths.get(uploadDir));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public ResponseDto uploadProfilePicture(UserServiceReqDto userServiceReqDto, MultipartFile file) {
        String userIdStr = userServiceReqDto.getUserId();

        // Validate user ID
        if (!isNumeric(userIdStr)) {
            return new ResponseDto("Enter numbers only for User ID.");
        }

        Long userId = Long.parseLong(userIdStr);
        logger.info("Uploading profile picture for user ID: {}", userId);

        try {
            // Check if a profile picture already exists
            if (profilePictureDao.getProfilePictureByUserId(userId) != null) {
                return new ResponseDto("A profile picture already exists for this user.");
            }

            // Validate file
            validateFile(file);

            // Handle file name and path
            String fileName = handleFileName(file);
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath);

            // Set file metadata in userServiceReqDto
            userServiceReqDto.setFilePath(filePath.toString());
            userServiceReqDto.setFileName(fileName);
            userServiceReqDto.setFileType(file.getContentType());

            // Convert DTO to ProfilePicture entity and save
            ProfilePicture profilePicture = userServiceReqDto.toProfilePicture();
            profilePictureDao.saveProfilePicture(profilePicture);

            logger.info("Profile picture uploaded successfully for user ID: {}", userId);
            return new ResponseDto("Profile picture uploaded successfully.");

        } catch (IOException e) {
            logger.error("File error during upload for user ID: {}", userId, e);
            return new ResponseDto("Unable to upload profile picture due to a file error.");
        } catch (Exception e) {
            logger.error("Unexpected error during upload for user ID: {}", userId, e);
            return new ResponseDto("An unexpected error occurred. Please try again later.");
        }
    }

    private String handleFileName(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Check if a file with the same name already exists
        int count = 1;
        while (Files.exists(filePath)) {
            // Append a count to the file name to make it unique
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            fileName = baseName + "_" + count + extension;
            filePath = Paths.get(uploadDir, fileName);
            count++;
        }

        return fileName;
    }

    public ResponseEntity<byte[]> downloadProfilePicture(Long userId) {
        logger.info("Downloading profile picture for user ID: {}", userId);
        try {
            ProfilePicture profilePicture = profilePictureDao.getProfilePictureByUserId(userId);
            if (profilePicture == null) {
                return createErrorResponse("No profile picture found for the given user ID.");
            }

            Path filePath = Paths.get(profilePicture.getFilePath());
            if (!Files.exists(filePath)) {
                return createErrorResponse("File not found for the given user ID.");
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", profilePicture.getFileType())
                    .header("Content-Disposition", "attachment; filename=\"" + profilePicture.getFileName() + "\"")
                    .body(fileBytes);

        } catch (IOException e) {
            logger.error("File read error for user ID: {}", userId, e);
            return createErrorResponse("File read error. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error during download for user ID: {}", userId, e);
            return createErrorResponse("An unexpected error occurred while retrieving the profile picture.");
        }
    }

    private ResponseEntity<byte[]> createErrorResponse(String message) {
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain")
                .body(message.getBytes());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded. Please provide a valid image.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large. Maximum allowed size is 5MB.");
        }
        if (!isValidImageFileType(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type. Only JPEG and PNG files are allowed.");
        }
    }

    private boolean isValidImageFileType(String contentType) {
        return "image/jpeg".equalsIgnoreCase(contentType) || "image/png".equalsIgnoreCase(contentType);
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
