package com.app.ares.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty(message = "Email caonnot be empty")
    @Email(message ="Invalid email. Please enter a valid email")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;

}
