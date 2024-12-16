package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.UserDto.ListUser;
import com.plenamente.sgt.domain.dto.UserDto.MyProfile;
import com.plenamente.sgt.domain.dto.UserDto.RegisterUser;
import com.plenamente.sgt.domain.entity.AdminTherapist;
import com.plenamente.sgt.domain.entity.Secretary;
import com.plenamente.sgt.domain.entity.Therapist;
import com.plenamente.sgt.domain.entity.User;
import com.plenamente.sgt.infra.repository.UserRepository;
import com.plenamente.sgt.infra.security.JwtService;
import com.plenamente.sgt.infra.security.LoginRequest;
import com.plenamente.sgt.infra.security.TokenResponse;
import com.plenamente.sgt.service.UserFactory;
import com.plenamente.sgt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest request) {
        // Limpia el contexto de seguridad antes de autenticar
        SecurityContextHolder.clearContext();

        // Autentica al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Establece el usuario autenticado en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Recupera el usuario desde el repositorio utilizando el username del request
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + request.getUsername()));

        // Verifica si el usuario está habilitado
        if (!user.isEnabled()) {
            throw new DisabledException("Este usuario ha sido deshabilitado.");
        }

        // Genera el token para el usuario autenticado
        String token = jwtService.getToken((UserDetails) authentication.getPrincipal(), user);

        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public TokenResponse addUser(RegisterUser data) {
        boolean isAlsoTherapist = data.paymentSession() != null;
        User user = UserFactory.createUser(data.role(), isAlsoTherapist);

        user.setName(data.name());
        user.setPaternalSurname(data.paternalSurname());
        user.setMaternalSurname(data.maternalSurname());
        user.setDni(data.dni());
        user.setPhone(data.phone());
        user.setPhoneBackup(data.phoneBackup());
        user.setAddress(data.address());
        user.setBirthdate(data.birthdate());
        user.setEmail(data.email());
        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setRol(data.role());

        // Lógica adicional para roles específicos
        if (user instanceof Therapist) {
            Double paymentSession = data.paymentSession();
            if (paymentSession != null) {
                ((Therapist) user).setPaymentSession(paymentSession);
            }
        }

        userRepository.save(user);

        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public List<ListUser> getAllUsers() {
        return userRepository.findAll().stream()
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
                        user.getAddress()  // nuevo campo
                ))
                .collect(Collectors.toList());
    }
    @Override
    public ListUser getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id: " + id));

        return new ListUser(
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
                user.getAddress()
        );
    }

    @Override
    public MyProfile getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));
        return new MyProfile(
                user.getName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getDni(),
                user.getAddress(),
                user.getPhone(),
                user.getPhoneBackup(),
                user.getEmail(),
                user.getRol()
        );
    }

    @Override
    public MyProfile updateMyProfile(String username, MyProfile myProfileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // Actualizar datos del usuario
        user.setName(myProfileDto.name());
        user.setPaternalSurname(myProfileDto.paternalSurname());
        user.setMaternalSurname(myProfileDto.maternalSurname());
        user.setDni(myProfileDto.dni());
        user.setAddress(myProfileDto.address());
        user.setPhone(myProfileDto.phone());
        user.setPhoneBackup(myProfileDto.phoneBackup());
        user.setEmail(myProfileDto.email());

        userRepository.save(user); // Guardar cambios

        return new MyProfile(
                user.getName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getDni(),
                user.getAddress(),
                user.getPhone(),
                user.getPhoneBackup(),
                user.getEmail(),
                user.getRol()
        );
    }
}