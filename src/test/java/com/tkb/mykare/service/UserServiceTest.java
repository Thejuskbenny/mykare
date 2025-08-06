package com.tkb.mykare.service;

import com.tkb.mykare.dto.IpLocationDto;
import com.tkb.mykare.dto.UserRegistrationDto;
import com.tkb.mykare.dto.UserResponseDto;
import com.tkb.mykare.entity.User;
import com.tkb.mykare.exception.UserAlreadyExistsException;
import com.tkb.mykare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IpLocationService ipLocationService;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;
    private User user;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto("John Doe", "john@example.com", User.Gender.MALE, "password123");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setGender(User.Gender.MALE);
        user.setPassword("encodedPassword");
        user.setIpAddress("192.168.1.1");
        user.setCountry("United States");
        user.setRole(User.Role.USER);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(ipLocationService.getCurrentIpAddress()).thenReturn("192.168.1.1");

        IpLocationDto locationDto = new IpLocationDto();
        locationDto.setCountry("United States");
        when(ipLocationService.getLocationByIp("192.168.1.1")).thenReturn(locationDto);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDto result = userService.registerUser(registrationDto);

        // Assert
        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(registrationDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(registrationDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testValidateUser_ValidCredentials() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        // Act
        boolean result = userService.validateUser("john@example.com", "password123");

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateUser_InvalidCredentials() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        // Act
        boolean result = userService.validateUser("john@example.com", "wrongpassword");

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.validateUser("nonexistent@example.com", "password123");

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        // Act
        List<UserResponseDto> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals(user.getName(), result.get(0).getName());
        assertEquals(user2.getName(), result.get(1).getName());
    }

    @Test
    void testDeleteUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.deleteUserByEmail("john@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository).deleteByEmail("john@example.com");
    }

    @Test
    void testDeleteUserByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.deleteUserByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository, never()).deleteByEmail(anyString());
    }
}
