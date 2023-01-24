package com.project.bookstore.repository;

import com.project.bookstore.model.BookOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookOrderRepo extends JpaRepository<BookOrder, Integer> {

}
