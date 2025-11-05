package com.sv.youapp.bff;

import com.sv.youapp.bff.properties.BackEndForFrontEndProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({BackEndForFrontEndProperties.class})
@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class})
public class BackForFrontEndApplication {
  public static void main(String[] args) {
    SpringApplication.run(BackForFrontEndApplication.class, args);
  }
}
