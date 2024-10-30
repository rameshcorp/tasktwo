package com.TASKTWOO.TASKTOO.FACADE;

import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import com.TASKTWOO.TASKTOO.REPO.ProfilePictureRepository;
import com.TASKTWOO.TASKTOO.dto.ResponseDto;
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class ProfilePictureFacade {

    private final String uploadDir = "path/to/upload/directory"; // Update with actual directory path
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // Set max file size to 5 MB

    public ResponseDto uploadProfilePicture(UserServiceReqDto userServiceReqDto, MultipartFile file, ProfilePictureRepository profilePictureRepository) {
        Long userId = Long.parseLong(userServiceReqDto.getUserId());

        try {
            if (profilePictureRepository.findByUserId(userId) != null) {
                return new ResponseDto("A profile picture already exists for this user.", null, null);
            }

            validateFile(file);

            String fileName = handleFileName(file);
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath);

            userServiceReqDto.setFilePath(filePath.toString());
            userServiceReqDto.setFileName(fileName);
            userServiceReqDto.setFileType(file.getContentType());

            ProfilePicture profilePicture = userServiceReqDto.toProfilePicture();
            profilePictureRepository.save(profilePicture);

            String uploadTime = LocalDateTime.now().toString();
            return new ResponseDto("Profile picture uploaded successfully.", file.getContentType(), uploadTime);

        } catch (IOException e) {
            return new ResponseDto("Unable to upload profile picture due to a system error. Please try again later.", null, null);
        } catch (Exception e) {
            return new ResponseDto("An unexpected error occurred. Please try again later.", null, null);
        }
    }

    public ResponseEntity<byte[]> downloadProfilePicture(Long userId, ProfilePictureRepository profilePictureRepository) {
        try {
            ProfilePicture profilePicture = profilePictureRepository.findByUserId(userId);
            if (profilePicture == null) {
                return createErrorResponse("No profile picture found for the given user ID.");
            }

            Path filePath = Paths.get(profilePicture.getFilePath());
            byte[] fileBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", profilePicture.getFileType())
                    .header("Content-Disposition", "attachment; filename=\"" + profilePicture.getFileName() + "\"")
                    .body(fileBytes);

        } catch (IOException e) {
            return createErrorResponse("Failed to download file due to a file error.");
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred while retrieving the profile picture.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded. Please provide a valid image.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large. Maximum allowed size is 5MB.");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Unsupported file type. Please upload an image.");
        }
    }

    private String handleFileName(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        if (Files.exists(filePath)) {
            fileName = System.currentTimeMillis() + "_" + fileName;
        }
        return fileName;
    }

    public ResponseEntity<byte[]> createErrorResponse(String message) {
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain")
                .body(message.getBytes());
    }
}
