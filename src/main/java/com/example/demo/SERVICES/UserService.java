package com.example.demo.SERVICES;

import com.example.demo.DTOs.CredentialsDto;
import com.example.demo.DTOs.SignUpDto;
import com.example.demo.DTOs.UserDto;
import com.example.demo.ENTITIES.Utilisateur;
import com.example.demo.Exceptions.AppException;
import com.example.demo.MAPPERS.UserMapper;
import com.example.demo.REPOSITORIES.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        Utilisateur user = userRepository.findByUsername(credentialsDto.username())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            UserDto userDto = userMapper.toUserDto(user);
            if(userDto.getRole() == null){
                userDto.setRole(user.getRole());
            }
            if(userDto.getEmail() == null){
                userDto.setEmail(user.getEmail());
            }
            return userDto;
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<Utilisateur> optionalUser = userRepository.findByUsername(userDto.username());

        if (optionalUser.isPresent()) {
            throw new AppException("User already exists", HttpStatus.BAD_REQUEST);
        }

        if(userDto.email()==null || userDto.email().isEmpty()){
            throw new AppException("Email is required", HttpStatus.BAD_REQUEST);
        }

        if(userDto.firstname() == null || userDto.firstname().isEmpty()){
            throw new AppException("Firstname is required", HttpStatus.BAD_REQUEST);
        }

        if(userDto.lastname() == null || userDto.lastname().isEmpty()){
            throw new AppException("Lastname is required", HttpStatus.BAD_REQUEST);
        }

        Utilisateur user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));
        Utilisateur savedUser = userRepository.save(user);
        UserDto userDto1 = userMapper.toUserDto(savedUser);
        userDto1.setRole(userDto.role());
        return userDto1;
    }

    public UserDto findByLogin(String username) {
        Utilisateur user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }
}
