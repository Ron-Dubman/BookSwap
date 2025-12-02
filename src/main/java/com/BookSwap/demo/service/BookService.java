package com.BookSwap.demo.service;

import com.BookSwap.demo.model.Book;
import com.BookSwap.demo.model.User;
import com.BookSwap.demo.repository.BookRepository;
import com.BookSwap.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

   public void saveBook(Book book, MultipartFile file, String username) throws IOException {
        User owner = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
        book.setOwner(owner);

        if(!file.isEmpty()){
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String secureFilename = UUID.randomUUID().toString() + extension;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path targetLocation = uploadPath.resolve(secureFilename);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            book.setPhotoURL(secureFilename);
        }
        bookRepository.save(book);
   }

   public List<Book> getAllAvailableBooks(){
        return bookRepository.findAll();
   }

   public List<Book> findMyBooks(String username){
        User user = userRepository.findByUsername(username).orElseThrow();
        return bookRepository.findByOwner(user);
   }
}
