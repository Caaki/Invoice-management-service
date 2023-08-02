package com.app.ares.service.implementation;

import com.app.ares.domain.Role;
import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;
import com.app.ares.form.UpdateForm;
import com.app.ares.repository.RoleRepository;
import com.app.ares.repository.UserRepository;
import com.app.ares.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.app.ares.dtomapper.UserDTOMapper.toUserDTO;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }
    @Override
    public void resetPassword(String email) {
        userRepository.resetPassword(email);
    }
    @Override
    public UserDTO verifyPasswordKey(String key) {
        return mapToUserDTO(userRepository.verifyPasswordKey(key));
    }
    @Override
    public void renewPassword(String key, String password, String confirmation) {
        userRepository.renewPassword(key, password, confirmation);
    }

    @Override
    public UserDTO verifyAccountCode(String code) {
        return mapToUserDTO(userRepository.verifyAccountCode(code));
    }

    @Override
    public UserDTO updateUserDetails(UpdateForm user) {
        return mapToUserDTO(userRepository.updateUserDetails(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return mapToUserDTO(userRepository.get(userId));
    }

    private UserDTO mapToUserDTO(User user){
        return toUserDTO(user, roleRepository.getRoleByUserId(user.getId()));
    }

}
