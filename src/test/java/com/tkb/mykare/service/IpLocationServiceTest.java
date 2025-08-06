package com.tkb.mykare.service;

import com.tkb.mykare.dto.IpLocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpLocationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IpLocationService ipLocationService;

    @BeforeEach
    void setUp() {
        // Use reflection to inject the mocked RestTemplate
        try {
            java.lang.reflect.Field field = IpLocationService.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(ipLocationService, restTemplate);
        } catch (Exception e) {
            fail("Failed to inject RestTemplate mock");
        }
    }

    @Test
    void testGetCurrentIpAddress_Success() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("192.168.1.1");

        // Act
        String result = ipLocationService.getCurrentIpAddress();

        // Assert
        assertEquals("192.168.1.1", result);
    }

    @Test
    void testGetCurrentIpAddress_Exception() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Connection failed"));

        // Act
        String result = ipLocationService.getCurrentIpAddress();

        // Assert
        assertEquals("Unknown", result);
    }

    @Test
    void testGetLocationByIp_Success() {
        // Arrange
        IpLocationDto mockLocation = new IpLocationDto();
        mockLocation.setIp("192.168.1.1");
        mockLocation.setCountry("United States");
        mockLocation.setStatus("success");

        when(restTemplate.getForObject(anyString(), eq(IpLocationDto.class), eq("192.168.1.1")))
                .thenReturn(mockLocation);

        // Act
        IpLocationDto result = ipLocationService.getLocationByIp("192.168.1.1");

        // Assert
        assertEquals("United States", result.getCountry());
        assertEquals("192.168.1.1", result.getIp());
    }

    @Test
    void testGetLocationByIp_Exception() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(IpLocationDto.class), eq("192.168.1.1")))
                .thenThrow(new RestClientException("Connection failed"));

        // Act
        IpLocationDto result = ipLocationService.getLocationByIp("192.168.1.1");

        // Assert
        assertEquals("Unknown", result.getCountry());
        assertEquals("192.168.1.1", result.getIp());
    }
}