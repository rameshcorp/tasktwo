package com.TASKTWOO.TASKTOO.CONTROLLER;

import com.TASKTWOO.TASKTOO.SERVICE.ProfilePictureService;
import com.TASKTWOO.TASKTOO.dto.ResponseDto;
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    @Autowired
    public ProfilePictureController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }
    @PostMapping("/upload")
    public ResponseDto uploadProfilePicture(
            @RequestParam MultipartFile file,
            @ModelAttribute UserServiceReqDto userServiceReqDto) {
        return profilePictureService.uploadProfilePicture(userServiceReqDto, file);
    }


    @GetMapping("/download/{userId}")
    public ResponseEntity<byte[]> downloadProfilePicture(@PathVariable Long userId) {
        return profilePictureService.downloadProfilePicture(userId);
    }
}
