package com.tkb.mykare.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkb.mykare.MykareApplication;
import com.tkb.mykare.dto.UserLoginDto;
import com.tkb.mykare.dto.UserRegistrationDto;
import com.tkb.mykare.entity.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(classes = MykareApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb-cucumber",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class StepDefinitions {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<String> lastResponse;
    private UserRegistrationDto userRegistrationDto;
    private UserLoginDto userLoginDto;
    private String baseUrl;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        baseUrl = "http://localhost:" + port + "/api/users";
    }

    @Given("I have user registration data with name {string}, email {string}, gender {string}, and password {string}")
    public void iHaveUserRegistrationData(String name, String email, String gender, String password) {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setName(name);
        userRegistrationDto.setEmail(email);
        userRegistrationDto.setGender(User.Gender.valueOf(gender));
        userRegistrationDto.setPassword(password);
    }

    @When("I send a POST request to register the user")
    public void iSendAPostRequestToRegisterTheUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = objectMapper.writeValueAsString(userRegistrationDto);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        lastResponse = restTemplate.postForEntity(baseUrl + "/register", request, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, lastResponse.getStatusCode().value());
    }

    @Then("the response should contain the user details")
    public void theResponseShouldContainTheUserDetails() {
        assertTrue(lastResponse.getBody().contains(userRegistrationDto.getName()));
        assertTrue(lastResponse.getBody().contains(userRegistrationDto.getEmail()));
    }

    @Given("I have login credentials with email {string} and password {string}")
    public void iHaveLoginCredentials(String email, String password) {
        userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(email);
        userLoginDto.setPassword(password);
    }

    @When("I send a POST request to login")
    public void iSendAPostRequestToLogin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = objectMapper.writeValueAsString(userLoginDto);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        lastResponse = restTemplate.postForEntity(baseUrl + "/login", request, String.class);
    }

    @Then("the response should contain {string}")
    public void theResponseShouldContain(String expectedMessage) {
        assertTrue(lastResponse.getBody().contains(expectedMessage));
    }

    @When("I send a GET request to fetch all users with admin credentials")
    public void iSendAGetRequestToFetchAllUsersWithAdminCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin@example.com", "admin");

        HttpEntity<String> request = new HttpEntity<>(headers);

        lastResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, request, String.class);
    }

    @When("I send a DELETE request to delete user with email {string} with admin credentials")
    public void iSendADeleteRequestToDeleteUserWithAdminCredentials(String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin@example.com", "admin");

        HttpEntity<String> request = new HttpEntity<>(headers);

        lastResponse = restTemplate.exchange(baseUrl + "/" + email, HttpMethod.DELETE, request, String.class);
    }
}