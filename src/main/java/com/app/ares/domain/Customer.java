package com.app.ares.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.Date;

import static jakarta.persistence.CascadeType.ALL;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @Email(message = "Invalid email. Please enter a valid email.")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    private String type;
    private String status;
    @NotEmpty(message = "Address cannot be empty")
    private String address;
    @NotEmpty(message = "Phone cannot be empty")
    private String phone;
    private String imageUrl;
    private Date createdAt;
    @OneToMany(mappedBy = "customer", fetch=FetchType.EAGER, cascade = ALL)
    private Collection<Invoice> invoices;

}
