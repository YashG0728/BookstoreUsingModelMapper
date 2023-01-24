package com.project.bookstore.dto;

import com.project.bookstore.model.Cart;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String name;
    private String phoneNumber;
    private int pinCode;
    private String locality;
    private String Address;
    private String city;
    private String landmark;
    private String addressType;
    private String userType;
    private String password;
    private Cart cart;
}
