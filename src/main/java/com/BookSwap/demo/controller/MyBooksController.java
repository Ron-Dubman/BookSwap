package com.BookSwap.demo.controller;

import com.BookSwap.demo.model.Book;
import com.BookSwap.demo.model.SwapRequest;
import com.BookSwap.demo.model.SwapStatus;
import com.BookSwap.demo.repository.BookRepository;
import com.BookSwap.demo.repository.SwapRequestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class MyBooksController {

    private final BookRepository bookRepository;
    private final SwapRequestRepository swapRequestRepository;

    public MyBooksController(BookRepository bookRepository,
                             SwapRequestRepository swapRequestRepository) {
        this.bookRepository = bookRepository;
        this.swapRequestRepository = swapRequestRepository;
    }

    @GetMapping("/my-books")
    public String myBooks(Model model,
                          Principal principal,
                          @RequestParam(value = "message", required = false) String message) {

        String username = principal.getName();

        List<Book> myBooks = bookRepository.findByOwnerUsername(username);
        // If your SwapStatus enum differs, adjust the status filter accordingly or provide a method without status.
        List<SwapRequest> incoming = swapRequestRepository
                .findByBookOwnerUsernameAndStatus(username, SwapStatus.PENDING);

        if (message != null) {
            model.addAttribute("message", message);
        }
        model.addAttribute("myBooks", myBooks);
        model.addAttribute("incomingRequests", incoming);

        return "my-books";
    }
}