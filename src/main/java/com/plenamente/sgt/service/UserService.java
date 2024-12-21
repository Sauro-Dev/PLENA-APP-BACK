package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.UserDto.*;
import com.plenamente.sgt.infra.security.LoginRequest;
import com.plenamente.sgt.infra.security.TokenResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    TokenResponse login(LoginRequest request);
    TokenResponse addUser(RegisterUser usuario);
    ListUser getUserById(Long id);
    List<ListUser> getAllUsers();
    MyProfile getMyProfile(String username);
    MyProfile updateMyProfile(String username, MyProfile myProfileDto);
    void updateCredentials(String currentUsername, @Valid CredentialsUpdate credentialsUpdate);
    void forgotPassword(@Valid ForgotPasswordRequest request);
}


