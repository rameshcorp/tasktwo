package com.TASKTWOO.TASKTOO.SERVICE;

import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;
import com.TASKTWOO.TASKTOO.REPO.ProfilePictureRepository;
import com.TASKTWOO.TASKTOO.dto.ResponseDto;
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import com.TASKTWOO.TASKTOO.FACADE.ProfilePictureFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfilePictureService implements IProfilePictureService {

    private final ProfilePictureRepository profilePictureRepository;
    private final ProfilePictureFacade profilePictureFacade;

    @Autowired
    public ProfilePictureService(ProfilePictureRepository profilePictureRepository, ProfilePictureFacade profilePictureFacade) {
        this.profilePictureRepository = profilePictureRepository;
        this.profilePictureFacade = profilePictureFacade;
    }

    @Override
    public ResponseDto uploadProfilePicture(UserServiceReqDto userServiceReqDto, MultipartFile file) {
        Long userId = parseUserId(userServiceReqDto);
        if (userId == null) return new ResponseDto("User ID is not valid.", null, null);
        return profilePictureFacade.uploadProfilePicture(userServiceReqDto, file, profilePictureRepository);
    }

    @Override
    public ResponseEntity<byte[]> downloadProfilePicture(Long userId) {
        if (userId == null || userId <= 0) {
            return profilePictureFacade.createErrorResponse("Invalid user ID.");
        }
        return profilePictureFacade.downloadProfilePicture(userId, profilePictureRepository);
    }

    private Long parseUserId(UserServiceReqDto userServiceReqDto) {
        try {
            return Long.parseLong(userServiceReqDto.getUserId());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
