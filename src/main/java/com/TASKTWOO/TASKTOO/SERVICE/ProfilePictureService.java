package com.TASKTWOO.TASKTOO.SERVICE;

import com.TASKTWOO.TASKTOO.dto.ResponseDto;
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import com.TASKTWOO.TASKTOO.FACADE.ProfilePictureFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfilePictureService implements IProfilePictureService {

    private final ProfilePictureFacade profilePictureFacade;

    @Autowired
    public ProfilePictureService(ProfilePictureFacade profilePictureFacade) {
        this.profilePictureFacade = profilePictureFacade;
    }

    @Override
    public ResponseDto uploadProfilePicture(UserServiceReqDto userServiceReqDto, MultipartFile file) {
        // Delegate the upload request to the facade
        return profilePictureFacade.uploadProfilePicture(userServiceReqDto, file);
    }

    @Override
    public ResponseEntity<byte[]> downloadProfilePicture(Long userId) {
        // Delegate the download request to the facade
        return profilePictureFacade.downloadProfilePicture(userId);
    }
}
