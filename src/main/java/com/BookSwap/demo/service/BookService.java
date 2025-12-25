package com.BookSwap.demo.service;

import com.BookSwap.demo.model.Book;
import com.BookSwap.demo.model.BookStatus;
import com.BookSwap.demo.model.User;
import com.BookSwap.demo.repository.BookRepository;
import com.BookSwap.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final String DEFAULT_COVER = "https://via.placeholder.com/300x450?text=No+Cover";

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveBook(Book book, String username) {

     if (book.getTitle() == null || book.getTitle().isBlank()) {
        throw new IllegalArgumentException("Book title is required");
    }
     if (book.getAuthor() == null || book.getAuthor().isBlank()) {
        throw new IllegalArgumentException("Book author is required");
    }
     if (book.getCondition() == null) {
        throw new IllegalArgumentException("Book condition is required");
    }
    if (book.getPhotoURL() == null || book.getPhotoURL().isBlank()) {
        book.setPhotoURL(DEFAULT_COVER);
    }
    
     User owner = userRepository.findByUsername(username)
           .orElseThrow(() -> new IllegalArgumentException("User not found"));
     book.setOwner(owner);
     book.setStatus(BookStatus.AVAILABLE);
     bookRepository.save(book);
        
   }

   public List<Book> getAllAvailableBooks(){
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
   }

   public List<Book> findMyBooks(String username){
        User user = userRepository.findByUsername(username).orElseThrow();
        return bookRepository.findByOwner(user);
   }

   public Book getBookForEdit(Long id, String username, boolean isAdmin) {
        if (isAdmin) {
           return bookRepository.findById(id)
                 .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        }
    
        User owner = userRepository.findByUsername(username).orElseThrow();
        return bookRepository.findByIdAndOwner(id, owner)
             .orElseThrow(() -> new IllegalArgumentException("Book not found or not owned by user"));
   }

   @Transactional
   public void updateBook(Long id, Book updated, String username, boolean isAdmin) {
       Book existing = getBookForEdit(id, username, isAdmin);

       if (updated.getTitle() == null || updated.getTitle().isBlank()) {
          throw new IllegalArgumentException("Book title is required");
       }
       if (updated.getAuthor() == null || updated.getAuthor().isBlank()) {
          throw new IllegalArgumentException("Book author is required");
       }
       if (updated.getCondition() == null) {
          throw new IllegalArgumentException("Book condition is required");
       }

       existing.setTitle(updated.getTitle());
       existing.setAuthor(updated.getAuthor());
       existing.setCondition(updated.getCondition());
       existing.setPhotoURL(
             (updated.getPhotoURL() == null || updated.getPhotoURL().isBlank())
                    ? DEFAULT_COVER
                    : updated.getPhotoURL()
       );

       bookRepository.save(existing);
   }

   @Transactional
   public void deleteBook(Long id, String username, boolean isAdmin) {
       Book existing = getBookForEdit(id, username, isAdmin);
       bookRepository.delete(existing);
   }
}
