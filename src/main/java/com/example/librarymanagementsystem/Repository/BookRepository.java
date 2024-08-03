package com.example.librarymanagementsystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.librarymanagementsystem.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Additional query methods can be added here if needed
}
