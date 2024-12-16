package com.plenamente.sgt.domain.dto.UserDto;

import com.plenamente.sgt.domain.entity.Rol;

public record ListUser(
        Long id,
        String username,
        String name,
        String email,
        Rol role,
        String paternalSurname,
        String maternalSurname,
        String dni,
        String phone,
        String phoneBackup,
        String address
) {}
