package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.UserDto.ListUser;
import com.plenamente.sgt.domain.dto.UserDto.MyProfile;
import com.plenamente.sgt.domain.dto.UserDto.RegisterUser;
import com.plenamente.sgt.infra.security.LoginRequest;
import com.plenamente.sgt.infra.security.TokenResponse;

import java.util.List;

public interface UserService {
    TokenResponse login(LoginRequest request);

    TokenResponse addUser(RegisterUser usuario);

    ListUser getUserById(Long id);

    List<ListUser> getAllUsers();

    MyProfile getMyProfile(String username); // Obtener perfil del usuario autenticado

    MyProfile updateMyProfile(String username, MyProfile myProfileDto); // Actualizar perfil del usuario autenticado
}


