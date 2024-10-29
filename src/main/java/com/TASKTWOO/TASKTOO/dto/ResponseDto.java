package com.TASKTWOO.TASKTOO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private String message;       // Message for success or error
    private String fileType;      // Type of the uploaded file
    private String uploadTime;    // Timestamp of when the file was uploaded
}
