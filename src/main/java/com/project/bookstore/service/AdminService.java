package com.project.bookstore.service;

import com.project.bookstore.dto.UserDTO;
import com.project.bookstore.dto.UserLoginDTO;
import com.project.bookstore.dto.UserRegistrationDTO;
import com.project.bookstore.exception.Exception;
import com.project.bookstore.model.BookOrder;
import com.project.bookstore.model.User;
import com.project.bookstore.repository.BookOrderRepo;
import com.project.bookstore.repository.UserRepo;
import com.project.bookstore.utility.JWTToken;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private JWTToken jwtToken;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookOrderRepo bookOrderRepo;

    public List<UserDTO> getAllUser(String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        List<UserDTO> list = userRepo.findAll().stream()
                .map(a -> modelMapper.map(a, UserDTO.class))
                .collect(Collectors.toList());
        if (list.size() == 0) {
            throw new Exception("No User exist in database.");
        }
        return list;
    }

    public List<BookOrder> getAllOrdersPlacedSuccessfully(String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        List<BookOrder> list = bookOrderRepo.findAll().stream()
                .filter(a -> a.getOrderStatus() == Boolean.TRUE)
                .toList();
        if (list.size() == 0) {
            throw new Exception("No order placed yet.");
        }
        return list;
    }

    public void deleteUsersByAdmin(String token, String phone) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        User userToBeDeleted = userRepo
                .findByPhoneNumber(phone)
                .orElseThrow(() -> new Exception("User does not Exists."));
        userRepo.delete(userToBeDeleted);
    }

}
