package com.example.librarymanagementsystem.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.librarymanagementsystem.Repository.BookRepository;
import com.example.librarymanagementsystem.Repository.BorrowingRecordRepository;
import com.example.librarymanagementsystem.Repository.PatronRepository;
import com.example.librarymanagementsystem.entity.Book;
import com.example.librarymanagementsystem.entity.BorrowingRecord;
import com.example.librarymanagementsystem.entity.Patron;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingRecordController {

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    public ResponseEntity<BorrowingRecord> borrowBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<Patron> patronOpt = patronRepository.findById(patronId);

        if (bookOpt.isPresent() && patronOpt.isPresent()) {
            Book book = bookOpt.get();
            Patron patron = patronOpt.get();

            if (!book.isAvailable()) {
                return ResponseEntity.status(400).body(null); // Bad Request if book is not available
            }

            BorrowingRecord borrowingRecord = new BorrowingRecord();
            borrowingRecord.setBook(book);
            borrowingRecord.setPatron(patron);
            borrowingRecord.setBorrowDate(LocalDate.now());

            // Mark the book as not available
            book.setAvailable(false);
            bookRepository.save(book);

            return ResponseEntity.ok(borrowingRecordRepository.save(borrowingRecord));
        } else {
            return ResponseEntity.notFound().build(); // Not Found if book or patron is missing
        }
    }

    @PutMapping("/return/{bookId}/patron/{patronId}")
    public ResponseEntity<BorrowingRecord> returnBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        // Use findAll() to get all records, and filter them based on your conditions
        List<BorrowingRecord> records = borrowingRecordRepository.findAll();

        Optional<BorrowingRecord> borrowingRecordOpt = records.stream()
                .filter(record -> record.getBook().getId().equals(bookId) 
                        /*((Book) record.getPatron()).getId().equals(patronId)*/ &&
                        record.getReturnDate() == null)
                .findFirst();

        if (borrowingRecordOpt.isPresent()) {
            BorrowingRecord borrowingRecord = borrowingRecordOpt.get();
            borrowingRecord.setReturnDate(LocalDate.now());

            // Mark the book as available
            Book book = borrowingRecord.getBook();
            book.setAvailable(true);
            bookRepository.save(book);

            return ResponseEntity.ok(borrowingRecordRepository.save(borrowingRecord));
        } else {
            return ResponseEntity.notFound().build(); // Not Found if borrowing record is missing
        }
    }
}
