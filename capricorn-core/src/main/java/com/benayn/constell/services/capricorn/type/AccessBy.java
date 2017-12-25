package com.benayn.constell.services.capricorn.type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class AccessBy {

    private Long userId;
    private String username;
    private String ip;

}
