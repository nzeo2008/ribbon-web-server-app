package com.example.demo.services;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.enums.ERole;
import com.example.demo.exceptions.UserExistException;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.repository.IUserRepository;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private final IUserRepository usersRepository;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserService(IUserRepository usersRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean findUserByEmail(String email) {
        return usersRepository.findUserEntityByEmail(email)
                .isPresent();
    }

    public String getJWT(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        return SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);
    }

    public UserEntity createUser(SignupRequest userIn) {

        if (usersRepository.findUserEntityByEmail(userIn.getEmail())
                .isPresent())
            throw new UserExistException("Пользователь с таким email: " + userIn.getEmail() + " уже существует");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userIn.getEmail());
        userEntity.setUsername(userIn.getUsername());
        userEntity.setFirstname(userIn.getFirstname());
        userEntity.setLastname(userIn.getLastname());
        userEntity.setPassword(passwordEncoder.encode(userIn.getPassword()));
        userEntity.getRoles()
                .add(ERole.ROLE_USER);

        usersRepository.save(userEntity);
        return userEntity;
    }

    public UserEntity updateUser(UserDTO userDTO,
                                 Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        userEntity.setFirstname(userDTO.getFirstname());
        userEntity.setLastname(userDTO.getLastname());
        userEntity.setUpdatedDate(LocalDateTime.now());

        return usersRepository.save(userEntity);
    }

    public UserEntity getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();

        return usersRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с никнеймом " + username + " не найден"));
    }

    public UserEntity getUserById(Long userId) {
        return usersRepository.findUserEntityById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
