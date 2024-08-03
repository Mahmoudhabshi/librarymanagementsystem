package com.example.librarymanagementsystem.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.librarymanagementsystem.Repository.PatronRepository;
import com.example.librarymanagementsystem.entity.Patron;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PatronService {

    private final PatronRepository patronRepository;

    @Autowired
    public PatronService(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    public Optional<Patron> getPatronById(Long id) {
        return patronRepository.findById(id);
    }

    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    public Patron updatePatron(Long id, Patron updatedPatron) {
        return patronRepository.findById(id)
                .map(patron -> {
                    patron.setName(updatedPatron.getName());
                    patron.setEmail(updatedPatron.getEmail());
                    patron.setName(updatedPatron.getName());
                    return patronRepository.save(patron);
                })
                .orElseThrow(() -> new RuntimeException("Patron not found with id " + id));
    }

    public void deletePatron(Long id) {
        patronRepository.deleteById(id);
    }
}
