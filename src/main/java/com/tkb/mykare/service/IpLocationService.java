package com.tkb.mykare.service;

import com.tkb.mykare.dto.IpLocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class IpLocationService {

    private static final Logger logger = LoggerFactory.getLogger(IpLocationService.class);
    private static final String IPIFY_URL = "https://api.ipify.org?format=text";
    private static final String IP_API_URL = "http://ip-api.com/json/{ip}";

    private final RestTemplate restTemplate;

    public IpLocationService() {
        this.restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
    }

    public String getCurrentIpAddress() {
        try {
            String ip = restTemplate.getForObject(IPIFY_URL, String.class);
            logger.info("Retrieved IP address: {}", ip);
            return ip != null ? ip.trim() : "Unknown";
        } catch (RestClientException e) {
            logger.error("Error fetching IP address: {}", e.getMessage());
            return "Unknown";
        }
    }

    public IpLocationDto getLocationByIp(String ipAddress) {
        try {
            IpLocationDto location = restTemplate.getForObject(IP_API_URL, IpLocationDto.class, ipAddress);
            if (location != null && "success".equals(location.getStatus())) {
                logger.info("Retrieved location for IP {}: {}", ipAddress, location.getCountry());
                return location;
            } else {
                logger.warn("Failed to get location for IP: {}", ipAddress);
                return createDefaultLocation(ipAddress);
            }
        } catch (RestClientException e) {
            logger.error("Error fetching location for IP {}: {}", ipAddress, e.getMessage());
            return createDefaultLocation(ipAddress);
        }
    }

    private IpLocationDto createDefaultLocation(String ipAddress) {
        IpLocationDto location = new IpLocationDto();
        location.setIp(ipAddress);
        location.setCountry("Unknown");
        return location;
    }
}