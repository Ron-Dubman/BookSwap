package com.BookSwap.demo.model;

public enum BookStatus {
    AVAILABLE,      // Default state
    PENDING_SWAP,   // Locked when a request is made 
    SWAPPED         // Final state 
}
