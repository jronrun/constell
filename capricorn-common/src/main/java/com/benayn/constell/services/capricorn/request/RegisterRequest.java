package com.benayn.constell.services.capricorn.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull
    @Size(min = 2, max = 255, message = "have to be grater than 2 characters")
    private String name;
    @NotNull
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "may not be valid")
    private String email;
    @NotNull
    private String passwd;
    @NotNull
    private Short gender;

}
