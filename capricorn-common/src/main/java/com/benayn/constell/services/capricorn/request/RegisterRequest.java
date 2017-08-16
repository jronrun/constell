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
    @ApiModelProperty("Username")
    private String name;
    @NotNull
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "may not be valid")
    @ApiModelProperty("E-mail")
    private String email;
    @NotNull
    @ApiModelProperty("Password")
    private String passwd;

}
