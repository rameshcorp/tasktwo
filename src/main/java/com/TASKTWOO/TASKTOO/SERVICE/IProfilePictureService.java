package com.TASKTWOO.TASKTOO.SERVICE;

import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IProfilePictureService {
    void uploadProfilePicture(Long userId, MultipartFile file) throws IOException;
    ProfilePicture getProfilePicture(Long userId);
}
