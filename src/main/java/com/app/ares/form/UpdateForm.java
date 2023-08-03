package com.app.ares.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateForm {

    @NotNull(message = "ID cannot be null or empty")
    private Long id;
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @Email(message = "Invalid email. Please enter a valid email.")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
    private String phone;
    private String address;
    private String title;
    private String bio;




}
