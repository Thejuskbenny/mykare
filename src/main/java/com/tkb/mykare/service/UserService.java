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

}