package com.benayn.constell.services.capricorn.type;

import java.util.List;
import lombok.Data;

@Data
public class Scope {

    private boolean anonymous;
    //1: 'Include All', 2: 'Include only in Define', 3: 'Exclude All', 4: 'Exclude only in Define'
    private Integer type;
    //user ids
    private List<Long> define;
}
