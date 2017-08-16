package com.benayn.constell.service.server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;

@Component
@Getter
@AllArgsConstructor(staticName = "of")
public class BenaynServiceInfo {

    private ApiInfo apiInfo;
    private String serviceName;

}