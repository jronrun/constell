package com.benayn.constell.services.capricorn;

import com.benayn.constell.service.server.service.BenaynMicroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CapricornApplication {

	public static void main(String[] args) {
        BenaynMicroService app = new BenaynMicroService();
        app.start(CapricornApplication.class, args);
    }

}
