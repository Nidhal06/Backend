package com.coworking.backend.service;

import com.coworking.backend.config.JwtTokenUtil;
import com.coworking.backend.dto.AuthRequest;
import com.coworking.backend.dto.AuthResponse;
import com.coworking.backend.dto.SignupRequest;
import com.coworking.backend.exception.BadRequestException;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.exception.UnauthorizedException;
import com.coworking.backend.model.PasswordResetToken;
import com.coworking.backend.model.User;
import com.coworking.backend.repository.PasswordResetTokenRepository;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.util.EmailService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.reset-password.expiration-hours}")
    private int resetTokenExpirationHours;

    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            String token = jwtTokenUtil.generateToken(userDetails);
            
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
            
            return new AuthResponse(token, user.getEmail(), user.getType().name(), user.getId());
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }
    
    public User registerUser(SignupRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        if(userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new BadRequestException("Phone Number is already in use!");
        }

        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setPhone(signUpRequest.getPhone());
        user.setType(User.UserType.COWORKER);
        user.setEnabled(true);

        return userRepository.save(user);
    }
    
    
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Generate token
        String token = UUID.randomUUID().toString();
        
        // Create and save token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(resetTokenExpirationHours));
        passwordResetTokenRepository.save(resetToken);

        // Send email
        sendResetPasswordEmail(user, token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the used token
        passwordResetTokenRepository.delete(resetToken);
    }

    private void sendResetPasswordEmail(User user, String token) {
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("name", user.getFirstName());
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("expirationHours", resetTokenExpirationHours);

        emailService.sendTemplateEmail(
            user.getEmail(),
            "Réinitialisation de votre mot de passe",
            "email/reset-password",
            context
        );
    }
}