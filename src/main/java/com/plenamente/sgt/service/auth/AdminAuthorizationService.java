package com.plenamente.sgt.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationService implements AuthorizationService {

    @Override
    public void authorizeRegisterUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("Acceso denegado: solo los administradores pueden registrar usuarios.");
        }
    }

}
