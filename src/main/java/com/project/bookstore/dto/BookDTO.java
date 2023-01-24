package com.project.bookstore.dto;

import lombok.Data;

@Data
public class BookDTO {
    private Integer id;
    private String bookCover;
    private Long price;
    private String bookName;
    private String bookAuthor;
    private Boolean stock;
    private Integer quantity;
}
