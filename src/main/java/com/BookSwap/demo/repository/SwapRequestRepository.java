package com.BookSwap.demo.repository;
import com.BookSwap.demo.model.User;
import com.BookSwap.demo.model.SwapRequest;
import com.BookSwap.demo.model.SwapStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {
    List<SwapRequest> findByBookOwner(User owner); // Incoming requests
    List<SwapRequest> findByRequester(User requester); // Outgoing requests
    List<SwapRequest> findByBookOwnerUsernameAndStatus(String username, SwapStatus status);
}
