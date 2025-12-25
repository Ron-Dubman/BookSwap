package com.BookSwap.demo.repository;
import com.BookSwap.demo.model.Book;
import com.BookSwap.demo.model.User;
import com.BookSwap.demo.model.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByOwnerNot(User user); // Find books I don't own (Marketplace)
    List<Book> findByOwner(User user);    // Find my books (Inventory)
    List<Book> findByStatus(BookStatus status); // Find books by status
    Optional<Book> findByIdAndOwner(Long id, User owner); // Guarded lookup for edit/delete


    List<Book> findByOwnerUsername(String username);
    List<Book> findByOwnerUsernameAndStatus(String username, BookStatus status);
    // CRITICAL SECURITY FEATURE: Pessimistic Lock
    // Prevents Race Conditions during the swap negotiation.
    // Generates SQL: SELECT * FROM books WHERE id=? FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithLock(@Param("id") Long id);
}