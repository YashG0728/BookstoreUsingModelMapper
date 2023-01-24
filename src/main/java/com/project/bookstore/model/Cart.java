package com.project.bookstore.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
@Data
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cart_id;
    @OneToMany(cascade = CascadeType.ALL)
    public List<BookOrder> books;
}