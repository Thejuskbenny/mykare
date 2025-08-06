package com.tkb.mykare.controller;

import com.tkb.mykare.dto.IpLocationDto;
import com.tkb.mykare.dto.UserLoginDto;
import com.tkb.mykare.dto.UserRegistrationDto;
import com.tkb.mykare.dto.UserResponseDto;
import com.tkb.mykare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user registration, authentication, and management")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user with basic details and capture IP/location information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserResponseDto user = userService.registerUser(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", registrationDto.getEmail(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Validate user credentials", description = "Validate user email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        boolean isValid = userService.validateUser(loginDto.getEmail(), loginDto.getPassword());

        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("message", "Login successful");
            response.put("email", loginDto.getEmail());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Get all users", description = "Retrieve all registered users (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Delete user by email", description = "Delete a user by email (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "Email of the user to delete", required = true)
            @PathVariable String email) {
        boolean deleted = userService.deleteUserByEmail(email);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate user by email", description = "Check if a user exists by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User exists"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> validateUser(
            @Parameter(description = "Email of the user to validate", required = true)
            @RequestParam String email) {
        boolean exists = userService.userExists(email);
        Map<String, String> response = new HashMap<>();
        if (exists) {
            response.put("message", "User exists");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        }
    }

    @GetMapping("/ip")
    @Operation(summary = "Get current user's IP address", description = "Retrieve the IP address of the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "IP address retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getCurrentUserIp() {
        try {
            String ipAddress = userService.getCurrentUserIp();
            Map<String, String> response = new HashMap<>();
            response.put("ipAddress", ipAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve IP address", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to retrieve IP address");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

    }

    @GetMapping("/location")
    @Operation(summary = "Get location by IP address", description = "Retrieve the location information based on the user's IP address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getLocationByIp(
            @Parameter(description = "IP address to get location for", required = true)
            @RequestParam String ipAddress) {
        try {
            IpLocationDto location = userService.getLocationByIp(ipAddress);
            if (location != null) {
                return ResponseEntity.ok(location);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Location not found for IP: " + ipAddress);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve location for IP: {}", ipAddress, e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to retrieve location for IP: " + ipAddress);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/location/current")
    @Operation(summary = "Get current user's location", description = "Retrieve the location information based on the current user's IP address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getCurrentUserLocation() {
        try {
            String ipAddress = userService.getCurrentUserIp();
            IpLocationDto location = userService.getLocationByIp(ipAddress);
            if (location != null) {
                return ResponseEntity.ok(location);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Location not found for IP: " + ipAddress);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve current user's location", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to retrieve current user's location");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check the health status of the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy"),
            @ApiResponse(responseCode = "500", description = "Service is unhealthy")
    })
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "Get user service information", description = "Retrieve basic information about the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service information retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getServiceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("service", "User Management Service");
        info.put("version", "1.0.0");
        info.put("description", "Service for managing user registrations, logins, and admin operations");
        return ResponseEntity.ok(info);
    }

    @GetMapping("/status")
    @Operation(summary = "Get user service status", description = "Retrieve the current status of the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service status retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getServiceStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "running");
        status.put("uptime", "24 hours");
        status.put("activeUsers", String.valueOf(userService.getActiveUserCount()));
        return ResponseEntity.ok(status);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get user service metrics", description = "Retrieve metrics related to user registrations, logins, and other statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getServiceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalUsers", userService.getTotalUserCount());
        metrics.put("activeUsers", userService.getActiveUserCount());
        metrics.put("recentRegistrations", userService.getRecentRegistrations());
        metrics.put("recentLogins", userService.getRecentLogins());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/config")
    @Operation(summary = "Get user service configuration", description = "Retrieve the current configuration settings of the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getServiceConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("maxLoginAttempts", String.valueOf(userService.getMaxLoginAttempts()));
        config.put("passwordMinLength", String.valueOf(userService.getPasswordMinLength()));
        config.put("registrationEnabled", String.valueOf(userService.isRegistrationEnabled()));
        return ResponseEntity.ok(config);
    }

    @GetMapping("/documentation")
    @Operation(summary = "Get user service API documentation", description = "Retrieve the API documentation for the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentation retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getApiDocumentation() {
        Map<String, String> documentation = new HashMap<>();
        documentation.put("swaggerUrl", "/v3/api-docs");
        documentation.put("redocUrl", "/redoc");
        documentation.put("swaggerUiUrl", "/swagger-ui.html");
        return ResponseEntity.ok(documentation);
    }

    @GetMapping("/support")
    @Operation(summary = "Get user service support information", description = "Retrieve support contact information for the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Support information retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getSupportInfo() {
        Map<String, String> supportInfo = new HashMap<>();
        supportInfo.put("email", "thejaskbenny@gmail.com");
        supportInfo.put("phone", "+919947497225");
        supportInfo.put("website", "https://mykare.com/support");
        return ResponseEntity.ok(supportInfo);
    }

    @GetMapping("/privacy")
    @Operation(summary = "Get user service privacy policy", description = "Retrieve the privacy policy for the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Privacy policy retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getPrivacyPolicy() {
        Map<String, String> privacyPolicy = new HashMap<>();
        privacyPolicy.put("policy", "We respect your privacy and are committed to protecting your personal information. Please read our full privacy policy at https://mykare.com/privacy");
        return ResponseEntity.ok(privacyPolicy);
    }

    @GetMapping("/terms")
    @Operation(summary = "Get user service terms of service", description = "Retrieve the terms of service for the user service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Terms of service retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getTermsOfService() {
        Map<String, String> termsOfService = new HashMap<>();
        termsOfService.put("terms", "By using our service, you agree to our terms of service. Please read the full terms at https://mykare.com/terms");
        return ResponseEntity.ok(termsOfService);
    }
}
