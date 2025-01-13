package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.UserDto.*;
import com.plenamente.sgt.domain.entity.User;
import com.plenamente.sgt.infra.security.LoginRequest;
import com.plenamente.sgt.infra.security.TokenResponse;
import com.plenamente.sgt.service.UserService;
import com.plenamente.sgt.service.auth.AuthorizationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthorizationService authorizationService;


    @PostMapping("/login")
    @Transactional
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<TokenResponse> addUser(@RequestBody @Valid RegisterUser data) {
        authorizationService.authorizeRegisterUser();
        return ResponseEntity.ok(userService.addUser(data));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/select/{id}")
    public ResponseEntity<ListUser> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateUserByAdmin(@PathVariable Long id, @RequestBody @Valid UpdateUserDto updateUserDto) {
        authorizationService.authorizeAdmin();
        userService.updateUserByAdmin(id, updateUserDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ListUser>> getAllUsers() {
        authorizationService.authorizeRegisterUser();
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/all-therapists")
    public ResponseEntity<List<ListUser>> getAllTherapists() {
        List<User> therapyCapableUsers = userService.getAllTherapyCapableUsers();
        List<ListUser> therapists = therapyCapableUsers.stream()
                .map(user -> new ListUser(
                        user.getIdUser(),
                        user.getUsername(),
                        user.getName(),
                        user.getEmail(),
                        user.getRol(),
                        user.getPaternalSurname(),
                        user.getMaternalSurname(),
                        user.getDni(),
                        user.getPhone(),
                        user.getPhoneBackup(),
                        user.getAddress(),
                        user.getBirthdate(),
                        user.isEnabled()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(therapists);
    }

    @GetMapping("/me")
    public ResponseEntity<MyProfile> getMyProfile() {
        String username = getAuthenticatedUsername();
        return ResponseEntity.ok(userService.getMyProfile(username));
    }

    @PutMapping("/me")
    public ResponseEntity<MyProfile> updateMyProfile(@RequestBody MyProfile myProfileDto) {
        String username = getAuthenticatedUsername();
        return ResponseEntity.ok(userService.updateMyProfile(username, myProfileDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-credentials")
    public ResponseEntity<Void> updateAdminCredentials(@RequestBody @Valid CredentialsUpdate credentialsUpdate) {
        String currentUsername = getAuthenticatedUsername();
        userService.updateCredentials(currentUsername, credentialsUpdate);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        String username = getAuthenticatedUsername();
        userService.updatePassword(username, passwordUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validate(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String email
    ) {
        boolean dniTaken = false;
        boolean emailTaken = false;

        if (dni != null && !dni.isBlank()) {
            dniTaken = userService.existsByDni(dni);
        }
        if (email != null && !email.isBlank()) {
            emailTaken = userService.existsByEmail(email);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("dniTaken", dniTaken);
        response.put("emailTaken", emailTaken);

        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String) {
            return (String) principal;
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            throw new IllegalStateException("Principal no es un tipo válido.");
        }
    }

    @GetMapping("/therapists")
    public List<User> getAllTherapyCapableUsers() {
        return userService.getAllTherapyCapableUsers();
    }
}

