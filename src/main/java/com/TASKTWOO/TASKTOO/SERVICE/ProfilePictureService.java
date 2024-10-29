package com.TASKTWOO.TASKTOO.SERVICE;
import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import com.TASKTWOO.TASKTOO.REPO.ProfilePictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Service
public class ProfilePictureService implements IProfilePictureService {

    private final ProfilePictureRepository profilePictureRepository;
    private final String uploadDir = "path/to/upload/directory"; // Change this to your upload directory

    @Autowired
    public ProfilePictureService(ProfilePictureRepository profilePictureRepository) {
        this.profilePictureRepository = profilePictureRepository;
        createUploadDir();
    }

    private void createUploadDir() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path); // Create the directory if it doesn't exist
            }
        } catch (IOException e) {
            // Log error (consider using a logging framework like SLF4J)
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }

    @Override
    public void uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        Path filePath = Paths.get(uploadDir, fileName);

        // Handle file name collisions
        if (Files.exists(filePath)) {
            String newFileName = System.currentTimeMillis() + "_" + fileName; // Append timestamp
            filePath = Paths.get(uploadDir, newFileName);
        }

        // Save the file to the specified directory
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new IOException("Failed to upload profile picture: " + e.getMessage(), e);
        }

        // Save metadata to the database
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUserId(userId);
        profilePicture.setFileName(fileName);
        profilePicture.setFileType(fileType);
        profilePicture.setFilePath(filePath.toString());
        profilePictureRepository.save(profilePicture);
    }

    @Override
    public ProfilePicture getProfilePicture(Long userId) {
        ProfilePicture profilePicture = profilePictureRepository.findByUserId(userId);
        System.out.println("Retrieved ProfilePicture: " + profilePicture);
        return profilePicture;
    }

}
