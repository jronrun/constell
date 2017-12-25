package com.benayn.constell.services.capricorn.type;

import lombok.Data;

@Data
public class AccessResult {

    // 1: 'Opened', 2: 'Executed', 3: 'Try Open', 4: 'Try Execute'
    private Integer code;
    private String message;
    private String request;
    private String response;

}
