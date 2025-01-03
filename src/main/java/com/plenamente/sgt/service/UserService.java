package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.UserDto.*;
import com.plenamente.sgt.domain.entity.User;
import com.plenamente.sgt.infra.security.LoginRequest;
import com.plenamente.sgt.infra.security.TokenResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    TokenResponse login(LoginRequest request);

    TokenResponse addUser(RegisterUser usuario);

    List<ListUser> getAllTherapists();

    ListUser getUserById(Long id);

    List<ListUser> getAllUsers();

    void updateUserByAdmin(Long id, UpdateUserDto updateUserDto);

    MyProfile getMyProfile(String username);

    MyProfile updateMyProfile(String username, MyProfile myProfileDto);

    void updateCredentials(String currentUsername, @Valid CredentialsUpdate credentialsUpdate);

    void updatePassword(String username, PasswordUpdateRequest passwordUpdateRequest);

    void forgotPassword(@Valid ForgotPasswordRequest request);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    List<User> getAllTherapyCapableUsers();

}


