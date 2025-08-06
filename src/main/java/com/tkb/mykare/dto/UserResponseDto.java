package com.tkb.mykare.dto;

import com.tkb.mykare.entity.User;

import java.time.LocalDateTime;

public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private User.Gender gender;
    private String ipAddress;
    private String country;
    private User.Role role;
    private LocalDateTime createdAt;

    // Constructors
    public UserResponseDto() {}

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.ipAddress = user.getIpAddress();
        this.country = user.getCountry();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public User.Gender getGender() { return gender; }
    public void setGender(User.Gender gender) { this.gender = gender; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}