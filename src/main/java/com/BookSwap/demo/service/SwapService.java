package com.BookSwap.demo.service;

import com.BookSwap.demo.model.*;
import com.BookSwap.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwapService {
    
    private final BookRepository bookRepository;
    private final SwapRequestRepository swapRepository;
    private final AuditLogRepository auditLogRepository;

     public SwapService(BookRepository br, SwapRequestRepository sr, AuditLogRepository ar) {
        this.bookRepository = br;
        this.swapRepository = sr;
        this.auditLogRepository = ar;
    }

    @Transactional
    public void requestSwap(User requester, Long bookId){
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found!"));
        
        if(book.getStatus() != BookStatus.AVAILABLE){
            throw new IllegalStateException("Book is not available for swap!");
        }

        SwapRequest request = new SwapRequest();
        request.setRequester(requester);
        request.setBook(book);
        request.setStatus(SwapStatus.PENDING);

        book.setStatus(BookStatus.PENDING_SWAP);

        swapRepository.save(request);
        bookRepository.save(book);
    }

    @Transactional
    public void acceptSwap(Long requestId, String username, String ipAddress){
        SwapRequest request = swapRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Swap request not found!"));
        
        Book book = bookRepository.findByIdWithLock(request.getBook().getId())
            .orElseThrow(() -> new RuntimeException("Book not found!"));
        
        if(!book.getOwner().getUsername().equals(username)){
            throw new SecurityException("You dont own this book!");
        }

        if (request.getStatus() != SwapStatus.PENDING){
            throw new IllegalStateException("Request is already processed!");
        }

        request.setStatus(SwapStatus.ACCEPTED);
        book.setStatus(BookStatus.SWAPPED);

        swapRepository.save(request);
        bookRepository.save(book);

        AuditLog log = new AuditLog();
        log.setAction("SWAP_ACCEPTED");
        log.setUsername(username);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);

    }
}
