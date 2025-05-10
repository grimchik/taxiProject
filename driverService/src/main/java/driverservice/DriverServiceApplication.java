package driverservice;

import driverservice.properties.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(KeycloakProperties.class)
public class DriverServiceApplication {
    public static void main (String[] args)
    {
        SpringApplication.run(DriverServiceApplication.class,args);
    }
}