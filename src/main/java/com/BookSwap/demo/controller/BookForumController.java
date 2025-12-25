package com.BookSwap.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.BookSwap.demo.service.BookService;

@Controller
public class BookForumController {

    @Autowired
    private BookService bookService;

    @GetMapping("/forum")
    public String viewForum(Model model){
        model.addAttribute("books", bookService.getAllAvailableBooks());
        return "forum";
    }
    
}