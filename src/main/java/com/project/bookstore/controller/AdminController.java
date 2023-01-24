package com.project.bookstore.controller;

import com.project.bookstore.dto.BookDTO;
import com.project.bookstore.dto.UserDTO;
import com.project.bookstore.dto.UserRegistrationDTO;
import com.project.bookstore.model.BookOrder;
import com.project.bookstore.service.AdminService;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private BookService bookService;

    @GetMapping("/bookList")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooksList();
    }

    @GetMapping("/userList/{Token}")
    public List<UserDTO> getAllUsersList(@PathVariable String Token) {
        return adminService.getAllUser(Token);
    }

    @PostMapping("/addBooks/{Token}")
    public ResponseEntity addBook(@RequestBody BookDTO bookDTO, @PathVariable String Token) {
        bookService.addBook(bookDTO, Token);
        return new ResponseEntity(Map.of("message", "Book added in Store."), HttpStatus.CREATED);
    }

    @PutMapping("/stockIn/{Token}")
    public ResponseEntity<BookDTO> inStock(@RequestBody BookDTO bookDTO, @PathVariable String Token) {
        bookService.inStock(bookDTO, Token);
        return new ResponseEntity(Map.of("message", bookDTO.getBookName() + " is in Stock."), HttpStatus.CREATED);
    }

    @PutMapping("/stockOut/{Token}")
    public ResponseEntity<BookDTO> outOfStock(@RequestBody BookDTO bookDTO, @PathVariable String Token) {
        bookService.outOfStock(bookDTO, Token);
        return new ResponseEntity(Map.of("message", bookDTO.getBookName() + " is out of Stock."), HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteBook/{Token}")
    public ResponseEntity<BookDTO> deleteBook(@RequestBody BookDTO bookDTO, @PathVariable String Token) {
        bookService.deleteBook(bookDTO, Token);
        return new ResponseEntity(Map.of("message", bookDTO.getBookName() + " is removed from Store."), HttpStatus.CREATED);
    }

    @GetMapping("/ordersPlaced/{Token}")
    public List<BookOrder> getListOfOrdersPlacedSuccessfully(@PathVariable String Token) {
        return adminService.getAllOrdersPlacedSuccessfully(Token);
    }

    @DeleteMapping("/deleteUserByAdmin/{Token}")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable String Token, String phone) {
        adminService.deleteUsersByAdmin(Token, phone);
        return new ResponseEntity(Map.of("message", phone + " account is deleted by Admin."), HttpStatus.CREATED);
    }


}
