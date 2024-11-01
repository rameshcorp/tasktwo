package com.TASKTWOO.TASKTOO.dao;

import com.TASKTWOO.TASKTOO.ENTITY.ProfilePicture;

public interface ProfilePictureDao {
    void saveProfilePicture(ProfilePicture profilePicture);
    ProfilePicture getProfilePictureByUserId(Long userId);
}
