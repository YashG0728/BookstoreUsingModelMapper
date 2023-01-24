package com.project.bookstore.controller;

import com.project.bookstore.dto.UserDTO;
import com.project.bookstore.dto.UserRegistrationDTO;
import com.project.bookstore.dto.UserLoginDTO;
import com.project.bookstore.model.BookOrder;
import com.project.bookstore.model.User;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDTO user) {
        userService.createUser(user);
        return new ResponseEntity(Map.of("message", "User Created Successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public String UserLogin(@RequestBody UserLoginDTO userLogin) {
        return userService.Login(userLogin);
    }

    @PostMapping("/logout/{Token}")
    public ResponseEntity<?> UserLogOut (@PathVariable String Token){
        userService.LogOut(Token);
        return new ResponseEntity(Map.of("message", "User Logged Out Successfully."), HttpStatus.OK);
    }

    @PostMapping("/addBookToCart/{Token}/{id}")
    public ResponseEntity<?> addBooksToCart(@PathVariable Integer id, @PathVariable String Token) {
        userService.addBooksToCart(id, Token);
        return new ResponseEntity(Map.of("message", "Book added to Cart."), HttpStatus.OK);
    }

    @PostMapping("/addBookCountToCart/{Token}/{id}")
    public void addCountToCart(@PathVariable Integer id, @PathVariable String Token) {
        userService.addQuantityToCart(id, Token);
    }

    @PostMapping("/removeBookCountFromCart/{Token}/{id}")
    public void removeCountFromCart(@PathVariable Integer id, @PathVariable String Token) {
        userService.removeQuantityFromCart(id, Token);
    }

    @DeleteMapping("/removeBookFromCart/{Token}/{id}")
    public ResponseEntity<?> removeBooksFromCart(@PathVariable Integer id, @PathVariable String Token) {
        userService.removeBooksFromCart(id, Token);
        return new ResponseEntity(Map.of("message", "Book removed from Cart."), HttpStatus.OK);
    }

    @GetMapping("/viewCart/{Token}")
    public List<BookOrder> viewCart(@PathVariable String Token) {
        return userService.viewCart(Token);
    }

    @PostMapping("/orderTheCart/{Token}")
    public ResponseEntity<?> orderBook(@PathVariable String Token) {
        userService.orderBook(Token);
        return new ResponseEntity(Map.of("message", "Order Placed Successfully"), HttpStatus.OK);
    }

    @DeleteMapping("/deleteAccount/{Token}")
    public ResponseEntity<?> deleteAccount(@PathVariable String Token) {
        userService.deleteAccount(Token);
        return new ResponseEntity(Map.of("message", "Account deleted Successfully"), HttpStatus.OK);
    }

    @GetMapping("/getUserDetails/{Token}")
    public UserDTO getUserDetails(@PathVariable String Token) {
        return userService.getUserDetails(Token);
    }

    @PutMapping("/updateUserDetails/{Token}")
    public User updateUserDetails(@RequestBody UserDTO userDTO, @PathVariable String Token) {
        return userService.updateUser(userDTO, Token);
//        return new ResponseEntity(Map.of("message", "Account updated Successfully"), HttpStatus.OK);
    }
}
