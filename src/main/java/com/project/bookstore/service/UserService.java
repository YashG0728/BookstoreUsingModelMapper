package com.project.bookstore.service;

import com.project.bookstore.dto.UserDTO;
import com.project.bookstore.dto.UserRegistrationDTO;
import com.project.bookstore.dto.UserLoginDTO;
import com.project.bookstore.exception.Exception;
import com.project.bookstore.model.Book;
import com.project.bookstore.model.BookOrder;
import com.project.bookstore.model.Cart;
import com.project.bookstore.model.User;
import com.project.bookstore.repository.BookOrderRepo;
import com.project.bookstore.repository.BookRepo;
import com.project.bookstore.repository.CartRepo;
import com.project.bookstore.repository.UserRepo;
import com.project.bookstore.utility.JWTToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookOrderRepo bookOrderRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JWTToken jwtToken;

    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        if(userRepo.findByPhoneNumber(userRegistrationDTO.getPhoneNumber()).isPresent()) {
            throw new Exception("Phone Number already exists");
        }
        userRegistrationDTO.setUserType("User");
        userRegistrationDTO.setCart(new Cart());
        User user = modelMapper.map(userRegistrationDTO, User.class);
        user.setLogStatus(Boolean.FALSE);
        userRepo.save(user);
    }

    public String Login(UserLoginDTO loginData) {
        User user = userRepo
                .findByPhoneNumber(loginData.getPhoneNumber())
                .orElseThrow(() -> new Exception("No User registered with : " + loginData.getPhoneNumber()));
        if(!user.getPassword().equals(loginData.getPassword())) {
                throw new Exception("Wrong User/Password.");
        }
        if(!user.getLogStatus()) {
            user.setLogStatus(Boolean.TRUE);
            userRepo.save(user);
        }
        else {
            throw new Exception("User already Logged in.");
        }

        return jwtToken.generateToken(loginData);
    }

    public void LogOut(String Token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(Token);
        User user = userRepo
                .findByPhoneNumber(userLoginDetails.getPhoneNumber())
                .orElseThrow(() -> new Exception("User not found."));
        if(user.getLogStatus()) {
            user.setLogStatus(Boolean.FALSE);
            userRepo.save(user);
        }
        else {
            throw new Exception("Need to Log in first.");
        }
    }

    public void addBooksToCart(Integer id, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        Optional<Book> bookToAddOld = bookRepo.findById(id);
        if(bookToAddOld.isEmpty()){
            throw new Exception("Book not found.");
        }
        Cart cart = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getCart();
        Optional<BookOrder> bookTwo = cart.books.stream()
                .filter(b -> b.getBookAuthor().equals(bookToAddOld.get().getBookAuthor()))
                .filter(b -> b.getBookName().equals(bookToAddOld.get().getBookName()))
                .findAny();
        if(bookTwo.isPresent()) {
            throw new Exception("book is already in cart.");
        }
        BookOrder bookToAddNew = modelMapper.map(bookToAddOld, BookOrder.class);
        bookToAddNew.setCountOfBook(1);
//        List<BookOrder> bookInBookOrderDTO = modelMapper
//                .map(bookToAdd, new TypeToken<List<BookOrder>>(){}.getType());
//        bookInBookOrderDTO.forEach(a -> a.setOrderStatus(Boolean.FALSE));
        cart.books.add(bookToAddNew);
        cartRepo.save(cart);
    }

    public void removeBooksFromCart(Integer id, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        Cart cart = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getCart();
        BookOrder bookFound = bookOrderRepo.findById(id)
                .orElseThrow(() -> new Exception("Book not found."));
        cart.books.remove(bookFound);
        bookOrderRepo.deleteById(id);
        cartRepo.save(cart);
    }

    public List<BookOrder> viewCart(String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        Cart cart = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getCart();
        return cart.books;
    }

    public void orderBook(String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        Cart cart = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getCart();
        cart.books.forEach(a -> a.setOrderStatus(Boolean.TRUE));
        cartRepo.save(cart);
        cart.books.clear();
        cartRepo.save(cart);
    }

    public void deleteAccount(String Token){
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(Token);
        User userToBeDeleted = userRepo
                .findByPhoneNumber(userLoginDetails.getPhoneNumber())
                .orElseThrow(() -> new Exception("User does not Exists."));
        if(userToBeDeleted.getLogStatus()) {
            userRepo.delete(userToBeDeleted);
        }
        else {
            throw new Exception("User not Logged in.");
        }
    }

    public UserDTO getUserDetails(String Token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(Token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        User userDetailsOld = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        return modelMapper.map(userDetailsOld, UserDTO.class);
    }

    public User updateUser(UserDTO userDTO, String Token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(Token);
        if (userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        System.out.println(userDTO);
        User originalDetails = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get();
        originalDetails.setName(userDTO.getName());
        originalDetails.setAddress(userDTO.getAddress());
        originalDetails.setLocality(userDTO.getLocality());
        originalDetails.setLandmark(userDTO.getLandmark());
        originalDetails.setPinCode(userDTO.getPinCode());
        originalDetails.setAddressType(userDTO.getAddressType());
        originalDetails.setCity(userDTO.getCity());
        userRepo.save(originalDetails);
        return originalDetails;
    }

    public void addQuantityToCart(Integer id, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        BookOrder bookFound = bookOrderRepo.findById(id)
                .orElseThrow(() -> new Exception("Book not found."));
        Book bookToAdd = bookRepo
                .findByBookNameAndBookAuthor(bookFound.getBookName(), bookFound.getBookAuthor()).get();
        if(bookToAdd == null){
            throw new Exception("Book not found.");
        }
        Cart cart = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getCart();
        Optional<BookOrder> bookTwo = cart.books.stream()
                .filter(b -> b.getBookAuthor().equals(bookToAdd.getBookAuthor()))
                .filter(b -> b.getBookName().equals(bookToAdd.getBookName()))
                .findAny();
        if(bookToAdd.getQuantity() == 0) {
            throw new Exception("Stock is Over for " + bookToAdd.getBookName());
        }
        if(bookTwo.isPresent() && bookToAdd.getQuantity() > 0) {
            bookToAdd.setQuantity(bookToAdd.getQuantity()-1);
            bookRepo.save(bookToAdd);
            bookTwo.get().setCountOfBook(bookTwo.get().getCountOfBook() + 1);
        }
        if(bookTwo.isEmpty()) {
            throw new Exception("Cart is Empty.");
        }
        cartRepo.save(cart);
    }

    public void removeQuantityFromCart(Integer id, String token) {
        UserLoginDTO userLoginDetails = jwtToken.decodeToken(token);
        if(userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getLogStatus() == Boolean.FALSE) {
            throw new Exception("User needs to log in first");
        }
        BookOrder bookFound = bookOrderRepo.findById(id)
                .orElseThrow(() -> new Exception("Book not found."));
        Book bookToRemove = bookRepo
                .findByBookNameAndBookAuthor(bookFound.getBookName(), bookFound.getBookAuthor()).get();
        if(bookToRemove == null){
            throw new Exception("Book not found.");
        }
        Cart cart = userRepo.findByPhoneNumber(userLoginDetails.getPhoneNumber()).get().getCart();
        Optional<BookOrder> bookTwo = cart.books.stream()
                .filter(b -> b.getBookAuthor().equals(bookToRemove.getBookAuthor()))
                .filter(b -> b.getBookName().equals(bookToRemove.getBookName()))
                .findAny();
        if(bookTwo.get().getCountOfBook() == 1) {
            throw new Exception("Stock is Over for " + bookToRemove.getBookName());
        }
        if(bookTwo.isPresent() && bookTwo.get().getCountOfBook() > 1) {
            bookToRemove.setQuantity(bookToRemove.getQuantity()+1);
            bookRepo.save(bookToRemove);
            bookTwo.get().setCountOfBook(bookTwo.get().getCountOfBook() - 1);
        }
        if(bookTwo.isEmpty()) {
            throw new Exception("Cart is Empty.");
        }
        cartRepo.save(cart);
    }
}
