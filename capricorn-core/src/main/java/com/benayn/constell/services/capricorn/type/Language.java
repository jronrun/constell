package com.benayn.constell.services.capricorn.type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Language {

    private String name;
    private String mime;

}
