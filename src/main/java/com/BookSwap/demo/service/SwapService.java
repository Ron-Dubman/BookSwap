package com.BookSwap.demo.service;

import com.BookSwap.demo.model.*;
import com.BookSwap.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class SwapService {
    private final BookRepository bookRepository;
    private final SwapRequestRepository swapRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public SwapService(BookRepository br, SwapRequestRepository sr, 
                       UserRepository ur, AuditLogRepository ar) {
        this.bookRepository = br;
        this.swapRepository = sr;
        this.userRepository = ur;
        this.auditLogRepository = ar;
    }

    @Transactional
    public SwapRequest requestSwap(String requesterUsername, Long bookId, String message) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new RuntimeException("Book is not available for swap");
        }

        if (book.getOwner().equals(requester)) {
            throw new RuntimeException("Cannot swap your own book");
        }

        SwapRequest swap = new SwapRequest();
        swap.setBook(book);
        swap.setRequester(requester);
        swap.setStatus(SwapStatus.PENDING);
        swap.setMessage(message);
        swap.setRequestedAt(LocalDateTime.now());

        return swapRepository.save(swap);
    }

    @Transactional
    public SwapRequest acceptSwap(Long swapId, String ownerUsername) {
        SwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));

        if (!swap.getBook().getOwner().getUsername().equals(ownerUsername)) {
            throw new RuntimeException("You are not the book owner");
        }

        swap.setStatus(SwapStatus.ACCEPTED);
        swap.setRespondedAt(LocalDateTime.now());
        swap.getBook().setStatus(BookStatus.SWAPPED);

        auditLogRepository.save(new AuditLog(
                "ACCEPT_SWAP",
                ownerUsername,
                "N/A" // IP Address can be set if HttpServletRequest is available
        ));

        return swapRepository.save(swap);
    }

    @Transactional
    public SwapRequest rejectSwap(Long swapId, String ownerUsername) {
        SwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));

        if (!swap.getBook().getOwner().getUsername().equals(ownerUsername)) {
            throw new RuntimeException("You are not the book owner");
        }

        swap.setStatus(SwapStatus.REJECTED);
        swap.setRespondedAt(LocalDateTime.now());

        return swapRepository.save(swap);
    }

    @Transactional
    public void completeSwap(Long swapId) {
        SwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));

        swap.setStatus(SwapStatus.ACCEPTED);
        swap.getBook().setStatus(BookStatus.AVAILABLE);
        swap.getBook().setOwner(swap.getRequester());

        swapRepository.save(swap);
    }
}