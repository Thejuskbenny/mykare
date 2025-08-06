package com.tkb.mykare.dto;

import com.tkb.mykare.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserRegistrationDto {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Gender is required")
    private User.Gender gender;

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public UserRegistrationDto() {}

    public UserRegistrationDto(String name, String email, User.Gender gender, String password) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.password = password;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public User.Gender getGender() { return gender; }
    public void setGender(User.Gender gender) { this.gender = gender; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}