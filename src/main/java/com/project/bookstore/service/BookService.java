package com.project.bookstore.service;

import com.project.bookstore.dto.BookDTO;
import com.project.bookstore.dto.UserLoginDTO;
import com.project.bookstore.exception.Exception;
import com.project.bookstore.model.Book;
import com.project.bookstore.model.User;
import com.project.bookstore.repository.BookRepo;
import com.project.bookstore.repository.UserRepo;
import com.project.bookstore.utility.JWTToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTToken jwtToken;
    @Autowired
    private ModelMapper modelMapper;

    public List<BookDTO> getAllBooksList() {
        List<BookDTO> list = bookRepo.findAll().stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
        if (list.size() == 0) {
            throw new Exception("Stock is empty.");
        }
        return list;
    }

    public void addBook(BookDTO bookDTO, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        if(bookRepo.findByBookNameAndBookAuthor(bookDTO.getBookName(), bookDTO.getBookAuthor()).isPresent()) {
            throw new Exception("Book exists in Book Store");
        }
        bookRepo.save(modelMapper.map(bookDTO, Book.class));
    }

    public void inStock(BookDTO bookDTO, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        Book book = bookRepo
                .findByBookNameAndBookAuthor(bookDTO.getBookName(), bookDTO.getBookAuthor())
                .orElseThrow(() -> new Exception("Book does not exist in Store."));
        if(book.getStock()) {
            throw new Exception("Book already in Stock.");
        }
        book.setStock(Boolean.TRUE);
        bookRepo.save(book);
    }

    public void outOfStock(BookDTO bookDTO, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        Book book = bookRepo
                .findByBookNameAndBookAuthor(bookDTO.getBookName(), bookDTO.getBookAuthor())
                .orElseThrow(() -> new Exception("Book does not exist in Store."));
        if(!book.getStock()) {
            throw new Exception("Book out of Stock.");
        }
        book.setStock(Boolean.FALSE);
        bookRepo.save(book);
    }

    public void deleteBook(BookDTO bookDTO, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        User user = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        if(user.getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        if(!user.getUserType().equalsIgnoreCase("Admin")) {
            throw new Exception("Not an Admin.");
        }
        Book book = bookRepo
                .findByBookNameAndBookAuthor(bookDTO.getBookName(), bookDTO.getBookAuthor())
                .orElseThrow(() -> new Exception("Book does not exist in Store."));
        bookRepo.delete(book);
    }

}
