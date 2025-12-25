package com.BookSwap.demo.controller;

import com.BookSwap.demo.service.SwapService;
import com.BookSwap.demo.model.SwapRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/swap")
public class SwapController {

    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @PostMapping("/request/{bookId}")
    public String requestSwap(@PathVariable Long bookId,
                              @RequestParam(required = false) String message,
                              Principal principal) {
        try {
            swapService.requestSwap(principal.getName(), bookId, message);
            return "redirect:/forum?message=Swap+request+sent";
        } catch (RuntimeException e) {
            return "redirect:/forum?error=" + e.getMessage();
        }
    }

    @PostMapping("/{swapId}/accept")
    public String acceptSwap(@PathVariable Long swapId, Principal principal) {
        try {
            swapService.acceptSwap(swapId, principal.getName());
            return "redirect:/my-books?message=Swap+accepted";
        } catch (RuntimeException e) {
            return "redirect:/my-books?error=" + e.getMessage();
        }
    }

    @PostMapping("/{swapId}/reject")
    public String rejectSwap(@PathVariable Long swapId, Principal principal) {
        try {
            swapService.rejectSwap(swapId, principal.getName());
            return "redirect:/my-books?message=Swap+rejected";
        } catch (RuntimeException e) {
            return "redirect:/my-books?error=" + e.getMessage();
        }
    }
}