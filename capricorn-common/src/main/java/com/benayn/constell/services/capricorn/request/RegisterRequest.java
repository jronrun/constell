package com.benayn.constell.services.capricorn.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@ApiModel("User Register Request")
public class RegisterRequest {

    @NotNull
    @Size(min = 2, max = 255, message = "have to be grater than 2 characters")
    @ApiModelProperty(value = "Username", required = true)
    private String name;
    @NotNull
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "may not be valid")
    @ApiModelProperty(value = "E-mail", required = true)
    private String email;
    @NotNull
    @ApiModelProperty(value = "Password", required = true)
    private String passwd;

}
