package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.entity.Book;
import com.example.librarymanagementsystem.entity.BorrowingRecord;
import com.example.librarymanagementsystem.entity.Patron;
import com.example.librarymanagementsystem.Repository.BookRepository;
import com.example.librarymanagementsystem.Repository.BorrowingRecordRepository;
import com.example.librarymanagementsystem.Repository.PatronRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class BorrowingRecordService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;

    @Autowired
    public BorrowingRecordService(BorrowingRecordRepository borrowingRecordRepository,
                                BookRepository bookRepository,
                                PatronRepository patronRepository) {
        this.borrowingRecordRepository = borrowingRecordRepository;
        this.bookRepository = bookRepository;
        this.patronRepository = patronRepository;
    }

    public BorrowingRecord borrowBook(Long bookId, Long patronId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<Patron> patronOpt = patronRepository.findById(patronId);

        if (bookOpt.isPresent() && patronOpt.isPresent()) {
            Book book = bookOpt.get();
            Patron patron = patronOpt.get();

            if (!book.isAvailable()) {
                throw new RuntimeException("Book is not available for borrowing");
            }

            BorrowingRecord borrowingRecord = new BorrowingRecord();
            borrowingRecord.setBook(book);
            borrowingRecord.setPatron(patron);
            borrowingRecord.setBorrowDate(LocalDate.now());

            // Mark the book as not available
            book.setAvailable(false);
            bookRepository.save(book);

            return borrowingRecordRepository.save(borrowingRecord);
        } else {
            throw new RuntimeException("Book or Patron not found");
        }
    }

    public BorrowingRecord returnBook(Long bookId, Long patronId) {
        Optional<BorrowingRecord> borrowingRecordOpt = borrowingRecordRepository.findAll().stream()
                .filter(record -> record.getBook().getId().equals(bookId) &&
                        record.getPatron().getId().equals(patronId) &&
                        record.getReturnDate() == null)
                .findFirst();

        if (borrowingRecordOpt.isPresent()) {
            BorrowingRecord borrowingRecord = borrowingRecordOpt.get();
            borrowingRecord.setReturnDate(LocalDate.now());

            // Mark the book as available
            Book book = borrowingRecord.getBook();
            book.setAvailable(true);
            bookRepository.save(book);

            return borrowingRecordRepository.save(borrowingRecord);
        } else {
            throw new RuntimeException("Borrowing record not found or book already returned");
        }
    }
}
