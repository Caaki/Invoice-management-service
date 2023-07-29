package com.app.ares.service.implementation;

import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;
import com.app.ares.dtomapper.UserDTOMapper;
import com.app.ares.repository.UserRepository;
import com.app.ares.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.toUserDTO(userRepository.create(user));
    }
}
