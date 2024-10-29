package com.TASKTWOO.TASKTOO.CONTROLLER;
import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import com.TASKTWOO.TASKTOO.SERVICE.ProfilePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/profile")
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;
    @Autowired
    public ProfilePictureController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadProfilePicture(
            @RequestParam Long userId,
            @RequestParam MultipartFile file) {
        // Check if the file is empty
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file provided.");
        }
        try {
            // Call the service method to upload the profile picture
            profilePictureService.uploadProfilePicture(userId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile picture uploaded successfully.");
        } catch (IOException e) {
            // Handle any IO exceptions during upload
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture: " + e.getMessage());
        }
    }
    @GetMapping("/download/{userId}")
    public ResponseEntity<byte[]> downloadProfilePicture(@PathVariable Long userId) {
        try {
            System.out.println("Requesting profile picture for user ID: " + userId);
            ProfilePicture profilePicture = profilePictureService.getProfilePicture(userId);

            if (profilePicture != null) {
                System.out.println("Found profile picture for user ID: " + userId + " at path: " + profilePicture.getFilePath());
                Path filePath = Paths.get(profilePicture.getFilePath());

                // Check if the file exists
                if (!Files.exists(filePath)) {
                    System.err.println("File not found at path: " + filePath);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                byte[] fileBytes = Files.readAllBytes(filePath);
                return ResponseEntity.ok()
                        .header("Content-Type", profilePicture.getFileType())
                        .header("Content-Disposition", "attachment; filename=\"" + profilePicture.getFileName() + "\"")
                        .body(fileBytes);
            } else {
                System.err.println("No profile picture found for user ID: " + userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (IOException e) {
            System.err.println("Error retrieving profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}



