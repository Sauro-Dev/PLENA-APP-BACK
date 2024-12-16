package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.entity.Admin;
import com.plenamente.sgt.domain.entity.AdminTherapist;
import com.plenamente.sgt.domain.entity.Rol;
import com.plenamente.sgt.domain.entity.Secretary;
import com.plenamente.sgt.domain.entity.Therapist;
import com.plenamente.sgt.domain.entity.User;

public class UserFactory {
    public static User createUser(Rol role, boolean isAlsoTherapist) {
        return switch (role) {
            case THERAPIST -> new Therapist();
            case SECRETARY -> new Secretary();
            case ADMIN -> isAlsoTherapist ? new AdminTherapist() : new Admin(); // Crear AdminTherapist si es admin y terapeuta
            default -> throw new IllegalArgumentException("Rol no soportado: " + role);
        };
    }
}
