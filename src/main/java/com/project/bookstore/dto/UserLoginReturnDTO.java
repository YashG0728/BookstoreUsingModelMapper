package com.project.bookstore.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserLoginReturnDTO {
    private String userType;
    private String Token;
}
