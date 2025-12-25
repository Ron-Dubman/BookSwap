package com.BookSwap.demo.controller;
import com.BookSwap.demo.model.Book;
import com.BookSwap.demo.model.BookCondition;
import com.BookSwap.demo.model.BookStatus;
import com.BookSwap.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import org.springframework.security.core.Authentication;


@Controller
public class BookListingController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books/new")
    public String showBookListingForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("conditions", BookCondition.values());
        model.addAttribute("status", BookStatus.values());
        return "book-form";
    }

    @PostMapping("/books/new")
    public String listNewBook(@ModelAttribute Book book, Principal principal) {
        bookService.saveBook(book, principal.getName());
        return "redirect:/forum?listingSuccess";
    }

    @GetMapping("/books/{id}/edit")
    public String showEditForm(@PathVariable Long id, Principal principal, Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Book book = bookService.getBookForEdit(id, principal.getName(), isAdmin);
        model.addAttribute("book", book);
        model.addAttribute("conditions", BookCondition.values());
        model.addAttribute("status", BookStatus.values());
        return "book-form";
    }

    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, Principal principal, Model model, RedirectAttributes redirectAttributes, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        try {
            bookService.updateBook(id, book, principal.getName(), isAdmin);
            redirectAttributes.addFlashAttribute("message", "Listing updated successfully.");
            return "redirect:/my-books";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("book", book);
            model.addAttribute("conditions", BookCondition.values());
            model.addAttribute("status", BookStatus.values());
            model.addAttribute("error", ex.getMessage());
            return "book-form";
        }
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        bookService.deleteBook(id, principal.getName(), isAdmin);
        redirectAttributes.addFlashAttribute("message", "Listing deleted.");
        return "redirect:/my-books";
    }

    
}
