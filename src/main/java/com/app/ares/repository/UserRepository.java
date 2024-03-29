package com.app.ares.repository;

import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;
import com.app.ares.form.UpdateForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface UserRepository <T extends User>{

    T create(T user);
    Collection<T> list (int page, int pageSize);
    T get(Long id);
    T update(T user);
    Boolean delete(Long id);
    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
    User verifyCode(String email, String code);
    void resetPassword(String email);
    T verifyPasswordKey(String key);
    void renewPassword(String key, String password, String confirmation);
    void renewPassword(Long userId, String password, String confirmation);
    T verifyAccountCode(String code);
    T updateUserDetails(UpdateForm user, Long id);
    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);
    void updateAccountSettings(Long userId,Boolean enabled, Boolean notLocked);
    User toggleMfa(String email);
    void updateImage(UserDTO user, MultipartFile image);
}
