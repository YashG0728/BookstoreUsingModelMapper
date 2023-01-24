package com.project.bookstore.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue
    private Integer UserID;
    private String name;
    private String phoneNumber;
    private int pinCode;
    private String locality;    //area,society
    private String Address;
    private String city;
    private String landmark;
    private String addressType;  //home,office,etc
    private String userType;        //user
    private String password;
    private Boolean logStatus;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_id")
    private Cart cart;
}
