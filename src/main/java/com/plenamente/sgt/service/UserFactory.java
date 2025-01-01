package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.entity.Admin;
import com.plenamente.sgt.domain.entity.AdminTherapist;
import com.plenamente.sgt.domain.entity.Rol;
import com.plenamente.sgt.domain.entity.Secretary;
import com.plenamente.sgt.domain.entity.Therapist;
import com.plenamente.sgt.domain.entity.User;

public class UserFactory {
    public static User createUser(Rol role, boolean isAlsoTherapist) {
        User user = switch (role) {
            case THERAPIST -> new Therapist();
            case SECRETARY -> new Secretary();
            case ADMIN -> isAlsoTherapist ? new AdminTherapist() : new Admin();
            default -> throw new IllegalArgumentException("Rol no soportado: " + role);
        };

        user.setIsTherapist(isAlsoTherapist || role == Rol.THERAPIST);
        user.setRol(role);
        return user;
    }
}