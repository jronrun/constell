package com.benayn.constell.services.capricorn;

import com.benayn.constell.service.server.service.BenaynMicroService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CapricornTestConfiguration {

    public static void main(String[] args) {
        BenaynMicroService app = new BenaynMicroService();
        app.start(CapricornTestConfiguration.class, args);
    }
}
