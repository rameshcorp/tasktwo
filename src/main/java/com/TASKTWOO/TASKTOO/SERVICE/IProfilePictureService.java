package com.TASKTWOO.TASKTOO.SERVICE;

import com.TASKTWOO.TASKTOO.dto.ResponseDto; // Import the new UploadResponseDto
import com.TASKTWOO.TASKTOO.dto.UserServiceReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IProfilePictureService {
    ResponseDto uploadProfilePicture(UserServiceReqDto userServiceReqDto, MultipartFile file); // Update return type
    ResponseEntity<byte[]> downloadProfilePicture(Long userId);
}
