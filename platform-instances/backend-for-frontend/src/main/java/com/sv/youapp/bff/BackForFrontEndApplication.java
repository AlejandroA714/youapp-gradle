package com.sv.youapp.bff;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.runApplication;

@SpringBootApplication(
    // exclude = { ReactiveUserDetailsServiceAutoConfiguration.class }
)
public class BackForFrontEndApplication {
    public static void main(String[] args) {
        runApplication(BackForFrontEndApplication.class, args);
    }
}
