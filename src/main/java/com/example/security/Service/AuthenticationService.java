package com.example.security.Service;

import com.example.security.Entities.*;
import com.example.security.Repositories.UseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UseRepository useRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .pwd(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        useRepository.save(user);
        String generateToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(generateToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = useRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Could not find username " + request.getEmail()));
        String generateToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(generateToken)
                .build();
    }
}
