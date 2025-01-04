package com.plenamente.sgt;

import com.plenamente.sgt.domain.dto.UserDto.*;
import com.plenamente.sgt.domain.entity.Therapist;
import com.plenamente.sgt.domain.entity.Rol;
import com.plenamente.sgt.domain.entity.User;
import com.plenamente.sgt.infra.repository.UserRepository;
import com.plenamente.sgt.infra.security.JwtService;
import com.plenamente.sgt.infra.security.LoginRequest;
import com.plenamente.sgt.infra.security.TokenResponse;
import com.plenamente.sgt.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // Ya no necesitamos inicializar los mocks manualmente
        System.out.println("\n=== Iniciando nuevo test ===");
    }

    @Test
    void loginWithValidCredentialsReturnsTokenResponse() {
        // Arrange
        String username = "admin";
        String password = "admin123";
        LoginRequest request = new LoginRequest(username, password);

        Therapist user = new Therapist();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setFirstLogin(true);
        user.setRol(Rol.THERAPIST);

        // Mock sencillo del repositorio
        when(userRepository.findByUsername(username))
                .then(invocation -> {
                    System.out.println("Mock de repositorio llamado con: " + invocation.getArgument(0));
                    return Optional.of(user);
                });

        // Mock de autenticación
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any()))
                .then(invocation -> {
                    System.out.println("Mock de autenticación llamado");
                    return mockAuth;
                });

        when(jwtService.getToken(any(), any()))
                .then(invocation -> {
                    System.out.println("Mock de JWT llamado");
                    return "token";
                });

        // Prueba previa del mock
        System.out.println("Prueba previa del mock repository:");
        Optional<User> testUser = userRepository.findByUsername(username);
        System.out.println("Usuario encontrado en prueba previa: " + testUser.isPresent());

        // Act
        System.out.println("\nEjecutando login:");
        TokenResponse response = userService.login(request);

        // Assert
        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getToken()).isEqualTo("token");
                    assertThat(r.isFirstLogin()).isTrue();
                });
    }

    @Test
    void loginWithDisabledUserThrowsDisabledException() {
        // Arrange
        LoginRequest request = new LoginRequest("username", "password");
        Therapist user = new Therapist();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(false);

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(DisabledException.class, () -> userService.login(request));
    }

    @Test
    void addUserWithValidDataReturnsTokenResponse() {
        // Arrange
        RegisterUser data = new RegisterUser(
            "name",
            "paternalSurname",
            "maternalSurname",
            "123456789",
            "987654321",
            "12345678",
            "email@example.com",
            "address",
            LocalDate.of(1990, 1, 1),
            "username",
            "password",
            Rol.THERAPIST,
            100.0,  // Provide a value for paymentSession
            null
        );
        Therapist user = new Therapist();
        user.setUsername("username");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(Therapist.class))).thenReturn(user);
        when(jwtService.getToken(any(), any())).thenReturn("token");

        // Act
        TokenResponse response = userService.addUser(data);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token");
    }

    @Test
    void getUserByIdWithValidIdReturnsUser() {
        // Arrange
        Long userId = 1L;
        Therapist user = new Therapist();
        user.setIdUser(userId);
        user.setUsername("username");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ListUser result = userService.getUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
    }

    @Test
    void getUserByIdWithInvalidIdThrowsUsernameNotFoundException() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void loginWithInvalidCredentialsThrowsException() {
        // Arrange
        LoginRequest request = new LoginRequest("invalidUsername", "invalidPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Invalid credentials"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.login(request));
    }

    @Test
    void addUserWithMissingMandatoryFieldsThrowsException() {
        // Arrange
        RegisterUser data = new RegisterUser(
            null,
            "paternalSurname",
            "maternalSurname",
            "123456789",
            "987654321",
            "12345678",
            "email@example.com",
            "address",
            LocalDate.of(1990, 1, 1),
            "username",
            "password",
            Rol.THERAPIST,
            null,
            null
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(data));
    }

    @Test
    void getUserByIdWithNullIdThrowsException() {
        // Arrange
        Long userId = null;

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUserByAdminWithValidDataUpdatesUser() {
        // Arrange
        Long userId = 1L;
        String newPassword = "newPassword";
        String encodedPassword = "encodedPassword123";

        UpdateUserDto updateUserDto = new UpdateUserDto(
                "newUsername",
                newPassword,
                "newName",
                "newPaternalSurname",
                "newMaternalSurname",
                "12345678",
                "newEmail@example.com",
                "newAddress",
                "newPhone",
                "newPhoneBackup",
                LocalDate.of(1990, 1, 1),
                true
        );

        Therapist user = new Therapist();
        user.setIdUser(userId);

        // Mock del passwordEncoder para el nuevo password
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.updateUserByAdmin(userId, updateUserDto);

        // Assert
        assertThat(user.getUsername()).isEqualTo("newUsername");
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(user.getName()).isEqualTo("newName");

        // Verify
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    void updatePasswordWithInvalidCurrentPasswordThrowsException() {
        // Arrange
        String username = "username";
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("invalidCurrentPassword", "newPassword");
        Therapist user = new Therapist();
        user.setUsername(username);
        user.setPassword("encodedCurrentPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("invalidCurrentPassword", "encodedCurrentPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(username, passwordUpdateRequest));
    }

    @Test
    void forgotPasswordWithInvalidDniThrowsException() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("username", "invalidDni", "newPassword");
        Therapist user = new Therapist();
        user.setUsername("username");
        user.setDni("validDni");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.forgotPassword(request));
    }
}