package com.TASKTWOO.TASKTOO.SERVICE;

import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import com.TASKTWOO.TASKTOO.REPO.ProfilePictureRepository;
import com.TASKTWOO.TASKTOO.dto.ResponseDto;
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class ProfilePictureService implements IProfilePictureService {

    private final ProfilePictureRepository profilePictureRepository;
    private final String uploadDir = "path/to/upload/directory"; // Update with actual directory path
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // Set max file size to 5 MB

    @Autowired
    public ProfilePictureService(ProfilePictureRepository profilePictureRepository) {
        this.profilePictureRepository = profilePictureRepository;
        createUploadDir();
    }

    // Ensure the upload directory exists
    private void createUploadDir() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path); // Create directory if it does not exist
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseDto uploadProfilePicture(UserServiceReqDto userServiceReqDto, MultipartFile file) {
        Long userId;
        try {
            userId = Long.parseLong(userServiceReqDto.getUserId());
        } catch (NumberFormatException e) {
            return new ResponseDto("User ID is not valid. Please check and try again.", null, null);
        }

        try {
            // Check if profile picture already exists for the user
            if (profilePictureRepository.findByUserId(userId) != null) {
                return new ResponseDto("A profile picture already exists for this user.", null, null);
            }

            // Validate file
            if (file.isEmpty()) {
                return new ResponseDto("No file uploaded. Please provide a valid image.", null, null);
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return new ResponseDto("File too large. Maximum allowed size is 5MB.", null, null);
            }

            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                return new ResponseDto("Unsupported file type. Please upload an image.", null, null);
            }

            // Handle file naming and saving
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Ensure unique file name if the file already exists
            if (Files.exists(filePath)) {
                fileName = System.currentTimeMillis() + "_" + fileName;
                filePath = Paths.get(uploadDir, fileName);
            }

            Files.copy(file.getInputStream(), filePath);

            // Set file metadata in the request DTO
            userServiceReqDto.setFilePath(filePath.toString());
            userServiceReqDto.setFileName(fileName);
            userServiceReqDto.setFileType(file.getContentType());

            // Convert to ProfilePicture and save
            ProfilePicture profilePicture = userServiceReqDto.toProfilePicture();
            profilePictureRepository.save(profilePicture);

            // Return success response
            String uploadTime = LocalDateTime.now().toString();
            return new ResponseDto("Profile picture uploaded successfully.", file.getContentType(), uploadTime);

        } catch (IOException e) {
            return new ResponseDto("Unable to upload profile picture due to a system error. Please try again later.", null, null);
        } catch (Exception e) {
            return new ResponseDto("An unexpected error occurred. Please try again later.", null, null);
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadProfilePicture(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                String errorMessage = "Invalid user ID.";
                return ResponseEntity.ok()
                        .header("Content-Type", "text/plain")
                        .body(errorMessage.getBytes());
            }

            ProfilePicture profilePicture = profilePictureRepository.findByUserId(userId);
            if (profilePicture == null) {
                String errorMessage = "No profile picture found for the given user ID.";
                return ResponseEntity.ok()
                        .header("Content-Type", "text/plain")
                        .body(errorMessage.getBytes());
            }

            Path filePath = Paths.get(profilePicture.getFilePath());
            if (!Files.exists(filePath)) {
                String errorMessage = "No file exists for the given user ID.";
                return ResponseEntity.ok()
                        .header("Content-Type", "text/plain")
                        .body(errorMessage.getBytes());
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", profilePicture.getFileType())
                    .header("Content-Disposition", "attachment; filename=\"" + profilePicture.getFileName() + "\"")
                    .body(fileBytes);

        } catch (IOException e) {
            String errorMessage = "Failed to download file due to a file error.";
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain")
                    .body(errorMessage.getBytes());
        } catch (Exception e) {
            String errorMessage = "An unexpected error occurred while retrieving the profile picture.";
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain")
                    .body(errorMessage.getBytes());
        }
    }

}
