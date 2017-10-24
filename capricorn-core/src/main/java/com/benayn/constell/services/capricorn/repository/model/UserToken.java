package com.benayn.constell.services.capricorn.repository.model;

import lombok.Data;

@Data
public class UserToken {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String scope;
    private String jti;

}
