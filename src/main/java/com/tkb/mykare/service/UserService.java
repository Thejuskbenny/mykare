package com.tkb.mykare.service;

import com.tkb.mykare.dto.IpLocationDto;
import com.tkb.mykare.dto.UserRegistrationDto;
import com.tkb.mykare.dto.UserResponseDto;
import com.tkb.mykare.entity.User;
import com.tkb.mykare.exception.UserAlreadyExistsException;
import com.tkb.mykare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IpLocationService ipLocationService;

    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        logger.info("Attempting to register user with email: {}", registrationDto.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registrationDto.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setGender(registrationDto.getGender());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Get IP address and location
        String ipAddress = ipLocationService.getCurrentIpAddress();
        IpLocationDto location = ipLocationService.getLocationByIp(ipAddress);

        user.setIpAddress(ipAddress);
        user.setCountry(location.getCountry());
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return new UserResponseDto(savedUser);
    }

    public boolean validateUser(String email, String password) {
        logger.info("Validating user with email: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean isValid = passwordEncoder.matches(password, user.getPassword());
            logger.info("User validation result for {}: {}", email, isValid);
            return isValid;
        }

        logger.warn("User not found with email: {}", email);
        return false;
    }

    public List<UserResponseDto> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    public boolean deleteUserByEmail(String email) {
        logger.info("Attempting to delete user with email: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            userRepository.deleteByEmail(email);
            logger.info("User deleted successfully: {}", email);
            return true;
        }

        logger.warn("User not found for deletion: {}", email);
        return false;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public String getCurrentUserIp() {
        String ip = ipLocationService.getCurrentIpAddress();
        logger.info("Current user IP address: {}", ip);
        return ip != null ? ip.trim() : "Unknown";
    }

    public IpLocationDto getLocationByIp(String ipAddress) {
        logger.info("Fetching location for IP address: {}", ipAddress);
        IpLocationDto location = ipLocationService.getLocationByIp(ipAddress);
        if (location != null) {
            logger.info("Location for IP {}: {}", ipAddress, location.getCountry());
        } else {
            logger.warn("No location found for IP: {}", ipAddress);
        }
        return location;
    }

    public char[] getActiveUserCount() {
        long count = userRepository.count();
        logger.info("Active user count: {}", count);
        return String.valueOf(count).toCharArray();
    }

    public Object getTotalUserCount() {
        long count = userRepository.count();
        logger.info("Total user count: {}", count);
        return count;
    }

    public Object getRecentRegistrations() {
        logger.info("Fetching recent user registrations");
        List<User> recentUsers = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                .collect(Collectors.toList());
        return recentUsers.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    public Object getRecentLogins() {
        logger.info("Fetching recent user logins");
        // Assuming we have a login history, this method would return the recent logins.
        // For now, we will return an empty list as a placeholder.
        return List.of(); // Replace with actual login history retrieval logic
    }

    public char[] getMaxLoginAttempts() {
        // Assuming we have a way to track login attempts, this method would return the maximum login attempts.
        // For now, we will return a placeholder value.
        int maxAttempts = 5; // Replace with actual logic to retrieve max login attempts
        logger.info("Maximum login attempts: {}", maxAttempts);
        return String.valueOf(maxAttempts).toCharArray();
    }

    public char[] getPasswordMinLength() {
        // Assuming we have a configuration for minimum password length, this method would return that value.
        // For now, we will return a placeholder value.
        int minLength = 8; // Replace with actual logic to retrieve minimum password length
        logger.info("Minimum password length: {}", minLength);
        return String.valueOf(minLength).toCharArray();
    }

    public char[] isRegistrationEnabled() {
        // Assuming we have a configuration to check if registration is enabled, this method would return that status.
        // For now, we will return a placeholder value indicating registration is enabled.
        boolean isEnabled = true; // Replace with actual logic to check registration status
        logger.info("Is registration enabled: {}", isEnabled);
        return String.valueOf(isEnabled).toCharArray();
    }
}