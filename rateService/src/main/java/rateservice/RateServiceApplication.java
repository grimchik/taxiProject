package rateservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class RateServiceApplication {
    public static void main (String[] args)
    {
        SpringApplication.run(RateServiceApplication.class,args);
    }
}
