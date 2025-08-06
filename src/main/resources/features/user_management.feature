Feature: User Management API

  Background:
    Given the application is running

  Scenario: Register a new user successfully
    Given I have user registration data with name "John Doe", email "john.doe@example.com", gender "MALE", and password "password123"
    When I send a POST request to register the user
    Then the response status should be 201
    And the response should contain the user details

  Scenario: Register user with existing email
    Given I have user registration data with name "Admin User", email "admin@example.com", gender "MALE", and password "password123"
    When I send a POST request to register the user
    Then the response status should be 409

  Scenario: Login with valid credentials
    Given I have login credentials with email "admin@example.com" and password "admin"
    When I send a POST request to login
    Then the response status should be 200
    And the response should contain "Login successful"

  Scenario: Login with invalid credentials
    Given I have login credentials with email "admin@example.com" and password "wrongpassword"
    When I send a POST request to login
    Then the response status should be 401
    And the response should contain "Invalid credentials"

  Scenario: Get all users as admin
    When I send a GET request to fetch all users with admin credentials
    Then the response status should be 200

  Scenario: Delete a user as admin
    When I send a DELETE request to delete user with email "nonexistent@example.com" with admin credentials
    Then the response status should be 404